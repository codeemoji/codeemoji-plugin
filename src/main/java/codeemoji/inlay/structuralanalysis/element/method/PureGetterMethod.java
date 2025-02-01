package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.collector.simple.CESimpleReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PureGetterMethod extends CEProviderMulti<PureGetterMethodSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return """
                public class PureGetterMethodExample {
                                
                    private int attribute;
                    
                    public PureGetterMethodExample(int attribute){
                        this.attribute = attribute;
                    }
                    
                    private int getAttribute(){
                        return attribute;
                    }
                }
                """;
    }

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(
                new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element){
                        return isPureGetterMethod(element);
                    }


                },
                new CESimpleReferenceMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element){
                        return isPureGetterMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull CEConfigurableWindow<PureGetterMethodSettings> createConfigurable() {
        return new PureGetterMethodConfigurable();
    }

    private boolean isPureGetterMethod(PsiMethod method) {
        final PsiCodeBlock methodBody = method.getBody();
        final PsiStatement[] methodBodyStatements = methodBody != null ? methodBody.getStatements() : null;
        return !method.hasParameters() &&
                methodBody != null && methodBodyStatements.length == 1 &&
                methodBodyStatements[0] instanceof PsiReturnStatement returnStatement &&
                returnStatement.getReturnValue() instanceof PsiReferenceExpression referenceExpression &&
                referenceExpression.resolve() instanceof PsiField field &&
                Objects.equals(field.getContainingClass(), method.getContainingClass()) &&
                (!getSettings().isJavaBeansNamingConventionApplied() || followsJavaBeansGetterNamingConvention(field, method));
    }

    private boolean followsJavaBeansGetterNamingConvention(PsiField field, PsiMethod method) {
        return Objects.equals(
                ((field.getType().getCanonicalText().equals("boolean")) ? "is" : "get") + StringUtils.capitalise(field.getName()),
                method.getName()
        );
    }
}
