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

import static codeemoji.inlay.structuralinspection.StructuralInspectionSymbols.PURE_GETTER;

@SuppressWarnings("UnstableApiUsage")
public class PureGetterMethod extends CEProviderMulti<NoSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return """
                public class PureGetterDefinitionExample {
                                
                    private int attribute;
                    
                    public PureGetterDefinitionExample(int attribute){
                        this.attribute = attribute;
                    }
                    
                    private int getAttribute(){
                        return attribute;
                    }
                }
                
                public class PureGetterInvocationExample {
                                
                   public static void main(String[] args){
                        PureGetterDefinitionExample object = new PureGetterDefinitionExample(10);
                        System.out.println(object.getAttribute());
                   }
                }
                
                """;
    }



    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {

        return List.of(

                new CEMethodCollector(editor, getKeyId(), PURE_GETTER) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return pureGetterMethodPredicate().test(element);
                    }
                },

                new CEReferenceMethodCollector(editor, getKeyId(), PURE_GETTER) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return pureGetterMethodPredicate().test(element);
                    }
                }
        );
    }

    private Predicate<PsiMethod> pureGetterMethodPredicate() {

        return element -> {

            boolean needsHint = false;

            if (
                    element.getName().startsWith("get") &&
                            element.getBody() != null &&
                            element.getBody().getStatements().length == 1
            ) {

                if (element.getBody().getStatements()[0] instanceof PsiReturnStatement) {

                    List<PsiElement> statementChildren = Arrays.stream(element.getBody().getStatements()[0].getChildren()).filter(child -> !(child instanceof PsiWhiteSpace)).toList();

                    if (
                            statementChildren.size() == 3 &&
                                    statementChildren.get(0) instanceof PsiKeyword &&
                                    Objects.equals(statementChildren.get(0).getText(), "return") &&
                                    statementChildren.get(1) instanceof PsiReferenceExpression &&
                                    ((PsiReference) statementChildren.get(1)).resolve() instanceof PsiField &&
                                    ((PsiField) Objects.requireNonNull(((PsiReference) statementChildren.get(1)).resolve())).getType() == element.getReturnType() &&
                                    ((PsiField) Objects.requireNonNull(((PsiReference) statementChildren.get(1)).resolve())).getContainingClass() == element.getContainingClass() &&
                                    statementChildren.get(2) instanceof PsiJavaToken &&
                                    Objects.equals(statementChildren.get(2).getText(), ";")
                    ) {
                        needsHint = true;
                    }
                }
            }

            return needsHint;
        };
    }
}
