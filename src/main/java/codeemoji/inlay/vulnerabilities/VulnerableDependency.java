package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.DynamicInlayBuilder;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.collector.simple.CEDynamicReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CESymbol;
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

    private static final Map<String, CESymbol> SEVERITY_SYMBOLS = new HashMap<>();

    static {
        SEVERITY_SYMBOLS.put("LOW", VULNERABLE_LOW);
        SEVERITY_SYMBOLS.put("MEDIUM", VULNERABLE_MEDIUM);
        SEVERITY_SYMBOLS.put("HIGH", VULNERABLE_HIGH);
        SEVERITY_SYMBOLS.put("CRITICAL", VULNERABLE_CRITICAL);
    }

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
                createReferenceCollector(editor)
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

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableDependencySettings settings) {
        return new VulnerableDependencyConfigurable(settings);
    }

    public InlayPresentation isInvokingMethodVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo) {
        return isInvokingMethodVulnerable(method, project, externalInfo, new HashSet<>());
    }

    private InlayPresentation isInvokingMethodVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo, Set<PsiMethod> visitedMethods) {
        if (visitedMethods.contains(method)) {
            return null;
        }
        visitedMethods.add(method);

        PsiMethod[] externalFunctionalityInvokingMethods = CEUtils.collectExternalFunctionalityInvokingMethods(method);

        for (PsiMethod invokingMethod : externalFunctionalityInvokingMethods) {
            VulnerabilityResult result = isVulnerable(invokingMethod, project, externalInfo);
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
            VulnerabilityResult result = isVulnerable(externalMethod, project, externalInfo);
            if (result != null) {
                vulnerableDependencies.add(result.dependencyName);
            }
        }

        if (!vulnerableDependencies.isEmpty()) {
            return createMethodContainingVulnerableDependencyInlay(vulnerableDependencies.size());
        }

        if (getSettings().isCheckVulnerableDependecyApplied()) {
            return Arrays.stream(externalMethods)
                    .map(m -> isInvokingMethodVulnerable(m, project, externalInfo, visitedMethods))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private VulnerabilityResult isVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo) {
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
                            return new VulnerabilityResult(dependency, vulnerabilities.size(), scanner);
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

    private InlayPresentation createDependencyCallInlay(VulnerabilityResult result) {
        String tooltip = result.dependencyName + " has " + result.numberOfVulnerabilities + " vulnerabilities";
        return inlayBuilder.buildInlayWithEmoji(VULNERABLE_CRITICAL, result.scanner + "Scanner - ", tooltip);
    }

    private static class VulnerabilityResult {
        String dependencyName;
        int numberOfVulnerabilities;
        String scanner;

        VulnerabilityResult(String dependencyName, int numberOfVulnerabilities, String scanner) {
            this.dependencyName = dependencyName;
            this.numberOfVulnerabilities = numberOfVulnerabilities;
            this.scanner = scanner;
        }
    }
}
