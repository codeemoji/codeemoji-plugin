package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public boolean isExternalFunctionalityInvokingMethod(PsiMethod method, Project project) {
        // Collect non-recursive method calls
        PsiElement[] externalFunctionalityInvokingElements = PsiTreeUtil.collectElements(
                method.getNavigationElement(),
                externalFunctionalityInvokingElement ->
                        externalFunctionalityInvokingElement instanceof PsiMethodCallExpression methodCallExpression &&
                        methodCallExpression.resolveMethod() != null && !method.isEquivalentTo(methodCallExpression.resolveMethod())
        );

        // The method contains 1 or more "direct" method calls that originate from files defined in external packages
        if (
                externalFunctionalityInvokingElements.length > 0 &&
                Arrays.stream(externalFunctionalityInvokingElements)
                        .map(externalFunctionalityInvokingElement -> ((PsiMethodCallExpression) externalFunctionalityInvokingElement).resolveMethod())
                        .anyMatch(externalFunctionalityInvokingElement -> checkMethodExternality(externalFunctionalityInvokingElement, project))
        ) {
            return true;
        }

        // The method contains 1 or more "indirect" non-recursive method calls that might invoke methods that originate from files defined outside the currently opened project
        else {

            // Every method call contained in the currently analyzed method body is recursively checked for externality
            if (getSettings().isCheckMethodCallsForExternalityApplied()) {


                // The method changes state if any of the method calls contains 1 or more method calls that originate from files defined outside the currently opened project
                return externalFunctionalityInvokingElements.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingElements)
                                .map(externalFunctionalityInvokingElement -> ((PsiMethodCallExpression) externalFunctionalityInvokingElement).resolveMethod())
                                .filter(externalFunctionalityInvokingElement -> !checkMethodExternality((PsiMethod) externalFunctionalityInvokingElement.getNavigationElement(), project))
                                .anyMatch(externalFunctionalityInvokingElement -> isExternalFunctionalityInvokingMethod(externalFunctionalityInvokingElement, project));
            }

            // No method call contained in the currently analyzed method body is recursively checked for externality
            else {
                return false;
            }
        }
    }

    public boolean checkMethodExternality(PsiMethod element, Project project) {
        return element.getContainingFile() instanceof PsiJavaFile javaFile &&
                element.getContainingClass() != null &&
                javaFile.getPackageStatement() != null &&
                !javaFile.getPackageName().startsWith("java") &&
                !CEUtils.getSourceRootsInProject(project).contains(ProjectFileIndex.getInstance(element.getProject()).getSourceRootForFile(element.getNavigationElement().getContainingFile().getVirtualFile()));
    }

}