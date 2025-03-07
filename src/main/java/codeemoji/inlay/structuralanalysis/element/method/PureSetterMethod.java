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

public class PureSetterMethod extends CEProviderMulti<PureSetterMethodSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(
                new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element) {
                        return isPureSetterMethod(element);
                    }
                },
                new CESimpleReferenceMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element) {
                        return isPureSetterMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull CEConfigurableWindow<PureSetterMethodSettings> createConfigurable() {
        return new PureSetterMethodConfigurable();
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
