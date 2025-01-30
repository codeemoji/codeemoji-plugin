package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.PURE_SETTER_METHOD;

@SuppressWarnings("UnstableApiUsage")
public class PureSetterMethod extends CEProviderMulti<PureSetterMethodSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return """
                public class PureSetterMethodExample {
                                
                    private int attribute;
                    
                    public PureSetterMethodExample(int attribute){
                        this.attribute = attribute;
                    }
                    
                    private void setAttribute(int attribute){
                        this.attribute = attribute;
                    }
                }
                """;
    }

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(
                new CESimpleMethodCollector(editor, getKey(), PURE_SETTER_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element) {
                        return isPureSetterMethod(element);
                    }
                },
                new CEReferenceMethodCollector(editor, getKey(), PURE_SETTER_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element) {
                        return isPureSetterMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull PureSetterMethodSettings settings) {
        return new PureSetterMethodConfigurable(settings);
    }

    private boolean isPureSetterMethod(PsiMethod method) {
        final PsiCodeBlock methodBody = method.getBody();
        final PsiStatement[] methodBodyStatements = methodBody != null ? methodBody.getStatements() : null;
        return !method.isConstructor() &&
                method.hasParameters() &&
                method.getParameterList().getParameters().length == 1 &&
                methodBody != null && methodBodyStatements.length == 1 &&
                methodBodyStatements[0] instanceof PsiExpressionStatement expressionStatement &&
                expressionStatement.getExpression() instanceof PsiAssignmentExpression assignmentExpression &&
                assignmentExpression.getLExpression() instanceof PsiReferenceExpression leftReferenceExpression &&
                leftReferenceExpression.resolve() instanceof PsiField field &&
                Objects.equals(field.getContainingClass(), method.getContainingClass()) &&
                assignmentExpression.getRExpression() instanceof PsiReferenceExpression rightReferenceExpression &&
                rightReferenceExpression.resolve() instanceof PsiParameter parameter &&
                parameter.getType().getCanonicalText().equals(field.getType().getCanonicalText()) &&
                parameter.getName().equals(field.getName()) &&
                (!getSettings().isJavaBeansNamingConventionApplied() || followsJavaBeansSetterNamingConvention(field, method));
    }

    private boolean followsJavaBeansSetterNamingConvention(PsiField field, PsiMethod method) {
        return Objects.equals(("set" + StringUtils.capitalise(field.getName())), method.getName());
    }
}
