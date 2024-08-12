package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.DynamicInlayBuilder;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.collector.simple.CEDynamicReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static codeemoji.core.util.CEUtils.isVulnerable;
import static codeemoji.inlay.vulnerabilities.VulnerableDependencySymbols.*;

public class VulnerableDependency extends CEProviderMulti<VulnerableDependencySettings> {

    private DynamicInlayBuilder inlayBuilder;

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableDependencySettings settings) {
        return new VulnerableDependencyConfigurable(settings);
    }

    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {
        inlayBuilder = new DynamicInlayBuilder(editor);
        return Arrays.asList(
                vulnerableMethodCollector(editor),
                vulnerableDependencyCallCollector(editor),
                vulnerableMethodReferenceCollector(editor),
                indirectVulnerableMethodCollector(editor)
        );
    }

    private InlayHintsCollector vulnerableMethodCollector(Editor editor) {
        return new CEDynamicMethodCollector(editor) {
            @Override
            public InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isMethodUsingVulnerableDependencies(element, editor.getProject(), externalInfo);
            }
        };
    }

    private InlayHintsCollector vulnerableMethodReferenceCollector(Editor editor) {
        return new CEDynamicReferenceMethodCollector(editor) {
            @Override
            protected InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                InlayPresentation result = isMethodUsingVulnerableDependencies(element, editor.getProject(), externalInfo);
                if (result != null && !CEUtils.checkMethodExternality(element, editor.getProject())) {
                    return result;
                }
                return null;
            }
        };
    }

    private InlayHintsCollector vulnerableDependencyCallCollector(Editor editor) {
        return new CEDynamicReferenceMethodCollector(editor) {
            @Override
            protected InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isACallToVulnerableDependency(element, editor.getProject(), externalInfo);
            }
        };
    }

    private InlayHintsCollector indirectVulnerableMethodCollector(Editor editor) {
        return new CEDynamicMethodCollector(editor) {
            @Override
            public InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                if (getSettings().isCheckVulnerableDependecyApplied()) {
                    return isIndirectlyUsingVulnerableDependencies(element, editor.getProject(), externalInfo);
                }
                return null;
            }
        };
    }

    private InlayPresentation isMethodUsingVulnerableDependencies(PsiMethod method, Project project, Map<?, ?> externalInfo) {
        return isMethodUsingVulnerableDependencies(method, project, externalInfo, new HashSet<>());
    }
    private InlayPresentation isMethodUsingVulnerableDependencies(PsiMethod method, Project project, Map<?, ?> externalInfo, Set<PsiMethod> visitedMethods) {
        if (visitedMethods.contains(method)) {
            return null;
        }
        PsiMethod[] externalMethods = CEUtils.collectExternalFunctionalityInvokingMethods(method);
        Set<String> vulnerableDependencies = new HashSet<>();

        for (PsiMethod externalMethod : externalMethods) {
            InlayInfo result = isVulnerable(externalMethod, project, externalInfo);
            if (result != null) {
                vulnerableDependencies.add(result.dependencyName);
            }
        }

        if (!vulnerableDependencies.isEmpty()) {
            return vulnerableMethodInlay(vulnerableDependencies.size());
        }
        return null;
    }

    public InlayPresentation isACallToVulnerableDependency(PsiMethod method, Project project, Map<?, ?> externalInfo) {
        if (CEUtils.checkMethodExternality(method, project)) {
            InlayInfo result = CEUtils.isVulnerable(method, project, externalInfo);
            if (result != null) {
                return vulnerableDependencyCallInlay(result);
            }
        }

        return null;
    }

    private InlayPresentation isIndirectlyUsingVulnerableDependencies(PsiMethod method, Project project, Map<?, ?> externalInfo) {
        Set<PsiMethod> visitedMethods = new HashSet<>();
        PsiMethod[] externalMethods = CEUtils.collectExternalFunctionalityInvokingMethods(method);

        for (PsiMethod externalMethod : externalMethods) {
            if (!visitedMethods.contains(externalMethod) && !CEUtils.checkMethodExternality(externalMethod, project)) {
                InlayPresentation result = isMethodUsingVulnerableDependencies(externalMethod, project, externalInfo, visitedMethods);
                if (result != null) {
                    return indirectVulnerableMethodInlay();
                }
            }
        }

        return null;
    }

    // inlay parsers
    private InlayPresentation vulnerableMethodInlay(int vuln) {
        String tooltip = "The method is using " + vuln + " vulnerable " +
                (vuln == 1 ? "dependency" : "dependencies");
        return inlayBuilder.buildInlayWithEmoji(VULNERABLE_METHOD, tooltip, null);
    }

    private InlayPresentation vulnerableDependencyCallInlay(InlayInfo result) {
        String tooltip = result.dependencyName + " has " + result.numberOfVulnerabilities + " vulnerabilities";
        return inlayBuilder.buildInlayWithEmoji(VULNERABLE_DEPENDENCY_CALL, result.scanner + "Scanner - ", tooltip);
    }

    private InlayPresentation indirectVulnerableMethodInlay() {
        String tooltip = "Function calling function/s with vulnerable dependencies usage";
        return inlayBuilder.buildInlayWithEmoji(INDIRECT_VULNERABLE_METHOD, tooltip, null);
    }

}
