package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PureSetterMethod extends CEProvider<PureSetterMethodSettings> {

    @Override
    protected void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addSimpleMethodCollector(this::isPureSetterMethod);
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<PureSetterMethodSettings> createConfigurable() {
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
