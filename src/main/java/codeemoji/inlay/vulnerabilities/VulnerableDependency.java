package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.collector.simple.CEDynamicReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static codeemoji.core.util.CEUtils.isVulnerable;
import static codeemoji.inlay.vulnerabilities.VulnerableDependencySymbols.*;

public class VulnerableDependency extends CEProviderMulti<VulnerableDependencySettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull CEConfigurableWindow<VulnerableDependencySettings> createConfigurable() {
        return new VulnerableDependencyConfigurable();
    }

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        String key = getKey();
        return List.of(
                new VulnerableMethodCollector(editor, key),
                new VulnerableMethodReferenceCollector(editor, key),
                new IndirectVulnerableMethodCollector(editor, key),
                new VulnerableDependencyCallCollector(editor, key)
        );
    }

    private static class VulnerableMethodCollector extends CEDynamicMethodCollector {
        protected VulnerableMethodCollector(@NotNull Editor editor, String settingsKey) {
            super(editor, settingsKey);
        }

        @Override
        public InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            return isMethodUsingVulnerableDependencies(element,
                    getEditor().getProject(),
                    getExternalInfo(element));
        }

        protected InlayVisuals isMethodUsingVulnerableDependencies(PsiMethod method, Project project, Map<?, ?> externalInfo) {
            return isMethodUsingVulnerableDependencies(method, project, externalInfo, new HashSet<>());
        }

        protected InlayVisuals isMethodUsingVulnerableDependencies(PsiMethod method, Project project, Map<?, ?> externalInfo, Set<PsiMethod> visitedMethods) {
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

        // text parsers
        protected InlayVisuals vulnerableMethodInlay(int vuln) {
            String pt1 = CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.pt1.tooltip");
            String pt2 = CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.pt2.tooltip");
            String pt3_1 = CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.pt3singular.tooltip");
            String pt3_2 = CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.pt3plural.tooltip");
            String tooltip = pt1 + vuln + " " + pt2 + (vuln == 1 ? pt3_1 : pt3_2);
            return this.buildInlayWithEmoji(VULNERABLE_METHOD, tooltip, null);
        }


        protected InlayVisuals indirectVulnerableMethodInlay() {
            String tooltip = CEBundle.getString("inlay.vulnerabledependency.indirectvulnerable.tooltip");
            return this.buildInlayWithEmoji(INDIRECT_VULNERABLE_METHOD, tooltip, null);
        }
    }

    private static class VulnerableMethodReferenceCollector extends VulnerableMethodCollector {
        protected VulnerableMethodReferenceCollector(@NotNull Editor editor, String key) {
            super(editor, key);
        }

        @Override
        public InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            InlayVisuals result = this.isMethodUsingVulnerableDependencies(element,
                    getEditor().getProject(),
                    getExternalInfo(element));
            if (result != null && !CEUtils.checkMethodExternality(element, getEditor().getProject())) {
                return result;
            }
            return null;
        }
    }

    private class IndirectVulnerableMethodCollector extends VulnerableMethodCollector {
        protected IndirectVulnerableMethodCollector(@NotNull Editor editor, String settingsKey) {
            super(editor, settingsKey);
        }

        @Override
        public InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (getSettings().isCheckVulnerableDependecyApplied()) {
                return isIndirectlyUsingVulnerableDependencies(element,
                        getEditor().getProject(),
                        getExternalInfo(element));
            }
            return null;
        }

        private InlayVisuals isIndirectlyUsingVulnerableDependencies(PsiMethod method, Project project, Map<?, ?> externalInfo) {
            Set<PsiMethod> visitedMethods = new HashSet<>();
            PsiMethod[] externalMethods = CEUtils.collectExternalFunctionalityInvokingMethods(method);

            for (PsiMethod externalMethod : externalMethods) {
                if (!visitedMethods.contains(externalMethod) && !CEUtils.checkMethodExternality(externalMethod, project)) {
                    InlayVisuals result = isMethodUsingVulnerableDependencies(externalMethod, project, externalInfo, visitedMethods);
                    if (result != null) {
                        return indirectVulnerableMethodInlay();
                    }
                }
            }

            return null;
        }

    }

    private static class VulnerableDependencyCallCollector extends CEDynamicReferenceMethodCollector {
        protected VulnerableDependencyCallCollector(@NotNull Editor editor, String key) {
            super(editor, key);
        }

        @Override
        protected InlayVisuals createInlayFor(@NotNull PsiMethod method) {
            Project project = getEditor().getProject();
            if (CEUtils.checkMethodExternality(method, project)) {
                InlayInfo result = CEUtils.isVulnerable(method, project, getExternalInfo(method));
                if (result != null) {
                    return makeVulnerableDependencyCallInlay(result);
                }
            }

            return null;
        }

        private InlayVisuals makeVulnerableDependencyCallInlay(InlayInfo result) {
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
                            .append(CEBundle.getString("text.vulnerabledependency.call.severity." + severity.toLowerCase()));
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
            return this.buildInlayWithEmoji(VULNERABLE_DEPENDENCY_CALL,
                    result.scanner + scannerPrefix, tooltip);
        }

    }

}
