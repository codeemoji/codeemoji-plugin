package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.inlay.vulnerabilities.MethodAnalysisUtils;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.EXTERNAL_FUNCTIONALITY_INVOKING_METHOD;

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
                        return isExternalFunctionalityInvokingMethod(element, editor.getProject(), false, m -> true);
                    }
                },

                new CEReferenceMethodCollector(editor, getKeyId(), EXTERNAL_FUNCTIONALITY_INVOKING_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityInvokingMethod(element, editor.getProject(), true, m -> true);
                    }
                }
        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ExternalFunctionalityInvokingMethodSettings settings) {
        return new ExternalFunctionalityInvokingMethodConfigurable(settings);
    }

    public boolean isExternalFunctionalityInvokingMethod(PsiMethod method, Project project, boolean fromReferenceMethod, MethodChecker additionalCheck) {
        return isExternalFunctionalityInvokingMethod(method, project, fromReferenceMethod, additionalCheck, new HashSet<>());
    }

    private boolean isExternalFunctionalityInvokingMethod(PsiMethod method, Project project, boolean fromReferenceMethod, MethodChecker additionalCheck, Set<PsiMethod> visitedMethods) {
        if (visitedMethods.contains(method)) {
            return false; // We've already checked this method, avoid recursion
        }
        visitedMethods.add(method);

        if(fromReferenceMethod && !MethodAnalysisUtils.checkMethodExternality(method, project)){
            return false;
        }

        PsiElement[] externalFunctionalityInvokingElements = PsiTreeUtil.collectElements(
                method.getNavigationElement(),
                externalFunctionalityInvokingElement ->
                        externalFunctionalityInvokingElement instanceof PsiMethodCallExpression methodCallExpression &&
                                methodCallExpression.resolveMethod() != null && !method.isEquivalentTo(methodCallExpression.resolveMethod())
        );

        for (PsiElement element : externalFunctionalityInvokingElements) {
            PsiMethod calledMethod = ((PsiMethodCallExpression) element).resolveMethod();
            if (calledMethod != null && MethodAnalysisUtils.checkMethodExternality(calledMethod, project)) {
                if (additionalCheck == null || additionalCheck.check(calledMethod)) {
                    return true;
                }
            }
        }

        if (getSettings().isCheckMethodCallsForExternalityApplied()) {
            for (PsiElement element : externalFunctionalityInvokingElements) {
                PsiMethod calledMethod = ((PsiMethodCallExpression) element).resolveMethod();
                if (calledMethod != null && !MethodAnalysisUtils.checkMethodExternality(calledMethod, project)) {
                    if (isExternalFunctionalityInvokingMethod(calledMethod, project, fromReferenceMethod, additionalCheck, visitedMethods)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @FunctionalInterface
    public interface MethodChecker {
        boolean check(PsiMethod method);
    }

}