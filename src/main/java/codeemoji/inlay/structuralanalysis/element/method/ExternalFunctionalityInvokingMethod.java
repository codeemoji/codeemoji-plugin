package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.vulnerabilities.MethodAnalysisUtils;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.EXTERNAL_FUNCTIONALITY_INVOKING_METHOD;

@SuppressWarnings("UnstableApiUsage")
public class ExternalFunctionalityInvokingMethod extends CEProviderMulti<ExternalFunctionalityInvokingMethodSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {

        return List.of(
                new CEMethodCollector(editor, getKeyId(), EXTERNAL_FUNCTIONALITY_INVOKING_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityInvokingMethod(element, editor.getProject());
                    }
                },

                new CEReferenceMethodCollector(editor, getKeyId(), EXTERNAL_FUNCTIONALITY_INVOKING_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityInvokingMethod(element, editor.getProject());
                    }
                }
        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ExternalFunctionalityInvokingMethodSettings settings) {
        return new ExternalFunctionalityInvokingMethodConfigurable(settings);
    }

    private PsiMethod[] collectExternalFunctionalityInvokingMethods(PsiMethod method){
        return PsiTreeUtil.collectElementsOfType(method.getNavigationElement(), PsiMethodCallExpression.class)
                .stream()
                .distinct()
                .<PsiMethod>mapMulti((methodCallExpression, consumer) -> {
                    PsiMethod resolvedMethodCallExpression = methodCallExpression.resolveMethod();
                    if (resolvedMethodCallExpression != null && !method.isEquivalentTo(resolvedMethodCallExpression)) {
                        consumer.accept(resolvedMethodCallExpression);
                    }
                })
                .toArray(PsiMethod[]::new);
    }

    private boolean isExternalFunctionalityInvokingMethod(PsiMethod method, Project project) {

        PsiMethod[] externalFunctionalityInvokingMethods = collectExternalFunctionalityInvokingMethods(method);

        if(
                externalFunctionalityInvokingMethods.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingMethods).anyMatch(externalFunctionalityInvokingMethod -> MethodAnalysisUtils.checkMethodExternality(externalFunctionalityInvokingMethod, project))
        ){
            return true;
        }

        else {

            if (getSettings().isCheckMethodCallsForExternalityApplied()){
                return Arrays.stream(externalFunctionalityInvokingMethods)
                        .filter(externalFunctionalityInvokingMethod -> !MethodAnalysisUtils.checkMethodExternality(externalFunctionalityInvokingMethod, project))
                        .anyMatch(externalFunctionalityInvokingMethod -> isExternalFunctionalityInvokingMethod(externalFunctionalityInvokingMethod, project));
            }

            else{
                return  false;
            }
        }
    }


}