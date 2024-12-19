package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.CEDynamicInlayBuilder;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.collector.simple.CEDynamicReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CEBundle;
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

    private CEDynamicInlayBuilder inlayBuilder;

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
        inlayBuilder = new CEDynamicInlayBuilder(editor, getKey());
        return Arrays.asList(
                vulnerableMethodCollector(editor),
                vulnerableDependencyCallCollector(editor),
                vulnerableMethodReferenceCollector(editor),
                indirectVulnerableMethodCollector(editor)
        );
    }

    private InlayHintsCollector vulnerableMethodCollector(Editor editor) {
        return new CEDynamicMethodCollector(editor, getKey()) {
            @Override
            public InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isMethodUsingVulnerableDependencies(element, editor.getProject(), externalInfo);
            }
        };
    }

    private InlayHintsCollector vulnerableMethodReferenceCollector(Editor editor) {
        return new CEDynamicReferenceMethodCollector(editor, getKey()) {
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
        return new CEDynamicReferenceMethodCollector(editor, getKey()) {
            @Override
            protected InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isACallToVulnerableDependency(element, editor.getProject(), externalInfo);
            }
        };
    }

    private InlayHintsCollector indirectVulnerableMethodCollector(Editor editor) {
        return new CEDynamicMethodCollector(editor, getKey()) {
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
        String pt1 = CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.pt1.tooltip");
        String pt2 = CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.pt2.tooltip");
        String pt3_1 = CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.pt3singular.tooltip");
        String pt3_2 = CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.pt3plural.tooltip");
        String tooltip = pt1 + vuln + " " + pt2 + (vuln == 1 ? pt3_1 : pt3_2);
        return inlayBuilder.buildInlayWithEmoji(VULNERABLE_METHOD, tooltip, null);
    }

    private InlayPresentation vulnerableDependencyCallInlay(InlayInfo result) {
        StringBuilder tooltipBuilder = new StringBuilder(result.dependencyName + " " +
                CEBundle.getString("inlay.vulnerabledependency.call.has") + " ");

        String[] severities = {"CRITICAL", "HIGH", "MEDIUM", "LOW"};
        boolean firstSeverity = true;
        int totalVulnerabilities = 0;

        for (String severity : severities) {
            int count = result.severityCounts.getOrDefault(severity, 0);
            if (count > 0) {
                if (!firstSeverity) {
                    tooltipBuilder.append(", ");
                }
                tooltipBuilder.append(count).append(" ")
                        .append(CEBundle.getString("inlay.vulnerabledependency.call.severity." + severity.toLowerCase()));
                firstSeverity = false;
                totalVulnerabilities += count;
            }
        }

        tooltipBuilder.append(" ")
                .append(CEBundle.getString(totalVulnerabilities == 1 ?
                        "inlay.vulnerabledependency.call.vulnerability" :
                        "inlay.vulnerabledependency.call.vulnerabilities"));

        String tooltip = tooltipBuilder.toString();
        String scannerPrefix = CEBundle.getString("inlay.vulnerabledependency.call.scanner");
        return inlayBuilder.buildInlayWithEmoji(VULNERABLE_DEPENDENCY_CALL,
                result.scanner + scannerPrefix, tooltip);
    }

    private InlayPresentation indirectVulnerableMethodInlay() {
        String tooltip = CEBundle.getString("inlay.vulnerabledependency.indirectvulnerable.tooltip");
        return inlayBuilder.buildInlayWithEmoji(INDIRECT_VULNERABLE_METHOD, tooltip, null);
    }

}
