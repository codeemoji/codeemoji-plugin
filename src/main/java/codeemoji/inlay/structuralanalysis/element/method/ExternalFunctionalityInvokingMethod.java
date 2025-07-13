package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ExternalFunctionalityInvokingMethod extends CEProvider<ExternalFunctionalityInvokingMethodSettings> {

    @Override
    protected void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addSimpleMethodCollector(element ->
                isExternalFunctionalityInvokingMethod(element, editor.getProject()));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<ExternalFunctionalityInvokingMethodSettings> createConfigurable() {
        return new ExternalFunctionalityInvokingMethodConfigurable();
    }


    private boolean isExternalFunctionalityInvokingMethod(PsiMethod method, Project project) {

        PsiMethod[] externalFunctionalityInvokingMethods = CEUtils.collectExternalFunctionalityInvokingMethods(method);

        if (
                externalFunctionalityInvokingMethods.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingMethods).anyMatch(externalFunctionalityInvokingMethod -> CEUtils.checkMethodExternality(externalFunctionalityInvokingMethod, project))
        ) {
            return true;
        } else {

            if (getSettings().isCheckMethodCallsForExternalityApplied()) {
                return Arrays.stream(externalFunctionalityInvokingMethods)
                        .filter(externalFunctionalityInvokingMethod -> !CEUtils.checkMethodExternality(externalFunctionalityInvokingMethod, project))
                        .anyMatch(externalFunctionalityInvokingMethod -> isExternalFunctionalityInvokingMethod(externalFunctionalityInvokingMethod, project));
            } else {
                return false;
            }
        }
    }


}