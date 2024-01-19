package codeemoji.inlay.structuralinspection.method;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import static codeemoji.inlay.structuralinspection.StructuralInspectionSymbols.PURE_SETTER;


@SuppressWarnings("UnstableApiUsage")
public class PureSetterMethod extends CEProviderMulti<NoSettings> {


    @Nullable
    @Override
    public String getPreviewText() {
        return """
                public class PureSetterDefinitionExample {
                                
                    private int attribute;
                    
                    public PureSetterDefinitionExample(int attribute){
                        this.attribute = attribute;
                    }
                    
                    private int getAttribute(){
                        return attribute;
                    }
                    
                    private void setAttribute(int attribute){
                        this.attribute = attribute;
                    }
                }
                
                public class PureSetterInvocationExample {
                                
                   public static void main(String[] args){
                        PureSetterDefinitionExample object = new PureSetterDefinitionExample(10);
                        System.out.println("Original value is: " + object.getAttribute());
                        object.setAttribute(5);
                        System.out.println("Updated value is: " + object.getAttribute());
                   }
                }
                
                """;
    }

    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {
        return List.of(
                new CEMethodCollector(editor, getKeyId(), PURE_SETTER) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return pureSetterMethodPredicate().test(element);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId(), PURE_SETTER) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return pureSetterMethodPredicate().test(element);
                    }
                }
        );
    }

    private Predicate<PsiMethod> pureSetterMethodPredicate() {

        return element -> {

            boolean needsHint = false;

            if (
                    element.getName().startsWith("set") &&
                            Objects.equals(element.getReturnType(), PsiTypes.voidType()) &&
                            element.getParameters().length == 1 &&
                            element.getBody() != null &&
                            element.getBody().getStatements().length == 1 &&
                            element.getBody().getStatements()[0] instanceof PsiExpressionStatement
            ) {
                List<PsiElement> statementChildren = Arrays.stream(element.getBody().getStatements()[0].getChildren()).filter(child -> !(child instanceof PsiWhiteSpace)).toList();

                if (
                        statementChildren.size() == 2 &&
                                statementChildren.get(0) instanceof PsiAssignmentExpression &&
                                statementChildren.get(1) instanceof PsiJavaToken &&
                                Objects.equals(statementChildren.get(1).getText(), ";")
                ) {
                    statementChildren = Arrays.stream(statementChildren.get(0).getChildren()).filter(child -> !(child instanceof PsiWhiteSpace)).toList();

                    if (
                            statementChildren.size() == 3 &&
                                    statementChildren.get(0) instanceof PsiReferenceExpression &&
                                    ((PsiReference) statementChildren.get(0)).resolve() instanceof PsiField &&
                                    ((PsiField) Objects.requireNonNull(((PsiReference) statementChildren.get(0)).resolve())).getContainingClass() == element.getContainingClass() &&
                                    statementChildren.get(1) instanceof PsiJavaToken &&
                                    Objects.equals(statementChildren.get(1).getText(), "=") &&
                                    statementChildren.get(2) instanceof PsiReferenceExpression &&
                                    ((PsiReference) statementChildren.get(2)).resolve() instanceof PsiParameter &&
                                    ((PsiParameter) Objects.requireNonNull(((PsiReference) statementChildren.get(2)).resolve())).getType() == ((PsiField) Objects.requireNonNull(((PsiReference) statementChildren.get(0)).resolve())).getType() &&
                                    Objects.requireNonNull(((PsiReference) statementChildren.get(2)).resolve()).getParent().getContext() == element
                    ) {
                        needsHint = true;
                    }
                }
            }

            return needsHint;
        };
    }
}