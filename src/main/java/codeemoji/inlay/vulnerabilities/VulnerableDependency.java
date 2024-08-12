package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.DynamicInlayBuilder;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.collector.simple.CEDynamicReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.external.DependencyInfo;
import codeemoji.inlay.external.VulnerabilityInfo;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static codeemoji.inlay.vulnerabilities.VulnerableDependencySymbols.*;

public class VulnerableDependency extends CEProviderMulti<VulnerableDependencySettings> {

    private DynamicInlayBuilder inlayBuilder;


    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }


    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {
        inlayBuilder = new DynamicInlayBuilder(editor);
        return Arrays.asList(
                createCollector(editor),
                createReferenceCollector(editor),
                createReferenceCollector2(editor),
                createIndirectVulnerabilityCollector(editor)
        );
    }

    private InlayHintsCollector createCollector(Editor editor) {
        return new CEDynamicMethodCollector(editor) {
            @Override
            public InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isMethodUsingVulnerableDependencies(element, editor.getProject(), externalInfo);
            }
        };
    }

    private InlayHintsCollector createReferenceCollector(Editor editor) {
        return new CEDynamicReferenceMethodCollector(editor) {
            @Override
            protected InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isInvokingMethodVulnerable(element, editor.getProject(), externalInfo);
            }
        };
    }

    private InlayHintsCollector createReferenceCollector2(Editor editor) {
        return new CEDynamicReferenceMethodCollector(editor) {
            @Override
            protected InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                // Only show inlay if the method is not directly calling a vulnerable dependency
                InlayPresentation result = isMethodUsingVulnerableDependencies(element, editor.getProject(), externalInfo);
                if (result != null && !CEUtils.checkMethodExternality(element, editor.getProject())) {
                    return result;
                }
                return null;
            }
        };
    }

    private InlayHintsCollector createIndirectVulnerabilityCollector(Editor editor) {
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

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableDependencySettings settings) {
        return new VulnerableDependencyConfigurable(settings);
    }

    public InlayPresentation isInvokingMethodVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo) {
        if (CEUtils.checkMethodExternality(method, project)) {
            InlayInfo result = isVulnerable(method, project, externalInfo);
            if (result != null) {
                return createDependencyCallInlay(result);
            }
        }

        return null;
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
            return createMethodContainingVulnerableDependencyInlay(vulnerableDependencies.size());
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
                    return createIndirectVulnerableDependencyInlay();
                }
            }
        }

        return null;
    }

    private InlayInfo isVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo) {
        if (!CEUtils.checkMethodExternality(method, project)) {
            return null;
        }

        VirtualFile file = method.getNavigationElement().getContainingFile().getVirtualFile();
        if (file == null) {
            return null;
        }
        String normalizedPath = CEUtils.normalizeDependencyPath(file.getPath());

        for (Map.Entry<?, ?> entry : externalInfo.entrySet()) {
            if (entry.getKey() instanceof DependencyInfo dependencyInfo) {
                String name = dependencyInfo.getPath();
                String dependency = name.split("@")[0];
                if (dependency.equals(normalizedPath)) {
                    if (entry.getValue() instanceof ArrayList<?> cveList) {
                        List<VulnerabilityInfo> vulnerabilities = cveList.stream()
                                .filter(VulnerabilityInfo.class::isInstance)
                                .map(VulnerabilityInfo.class::cast)
                                .collect(Collectors.toList());

                        if (!vulnerabilities.isEmpty()) {
                            String scanner = String.valueOf(vulnerabilities.get(0).getScanner());
                            return new InlayInfo(dependency, vulnerabilities.size(), scanner);
                        }
                    }
                }
            }
        }
        return null;
    }

    private InlayPresentation createMethodContainingVulnerableDependencyInlay(int vuln) {
        String tooltip = "The method is using " + vuln + " vulnerable " +
                (vuln == 1 ? "dependency" : "dependencies");
        return inlayBuilder.buildInlayWithEmoji(VULNERABLE_MEDIUM, tooltip, null);
    }

    private InlayPresentation createDependencyCallInlay(InlayInfo result) {
        String tooltip = result.dependencyName + " has " + result.numberOfVulnerabilities + " vulnerabilities";
        return inlayBuilder.buildInlayWithEmoji(VULNERABLE_CRITICAL, result.scanner + "Scanner - ", tooltip);
    }

    private InlayPresentation createIndirectVulnerableDependencyInlay() {
        String tooltip = "Function calling function/s with vulnerable dependencies usage";
        return inlayBuilder.buildInlayWithEmoji(VULNERABLE_HIGH, tooltip, null);
    }


}
