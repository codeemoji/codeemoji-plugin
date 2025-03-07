package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.collector.simple.CESimpleReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ExternalFunctionalityInvokingMethod extends CEProviderMulti<ExternalFunctionalityInvokingMethodSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {

        return List.of(
                new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element){
                        return isExternalFunctionalityInvokingMethod(element, editor.getProject());
                    }
                },

                new CESimpleReferenceMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element){
                        return isExternalFunctionalityInvokingMethod(element, editor.getProject());
                    }
                }
        );
    }

    @Override
    public @NotNull CEConfigurableWindow<ExternalFunctionalityInvokingMethodSettings> createConfigurable() {
   return new ExternalFunctionalityInvokingMethodConfigurable();
    }


    private boolean isExternalFunctionalityInvokingMethod(PsiMethod method, Project project) {

        PsiMethod[] externalFunctionalityInvokingMethods = CEUtils.collectExternalFunctionalityInvokingMethods(method);

        if(
                externalFunctionalityInvokingMethods.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingMethods).anyMatch(externalFunctionalityInvokingMethod -> CEUtils.checkMethodExternality(externalFunctionalityInvokingMethod, project))
        ){
            return true;
        }

        else {

            if (getSettings().isCheckMethodCallsForExternalityApplied()){
                return Arrays.stream(externalFunctionalityInvokingMethods)
                        .filter(externalFunctionalityInvokingMethod -> !CEUtils.checkMethodExternality(externalFunctionalityInvokingMethod, project))
                        .anyMatch(externalFunctionalityInvokingMethod -> isExternalFunctionalityInvokingMethod(externalFunctionalityInvokingMethod, project));
            }

            else{
                return  false;
            }
        }
    }


}