package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.collector.simple.CEDynamicReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static codeemoji.core.util.CEUtils.isVulnerable;

public class VulnerableDependency extends CEProviderMulti<VulnerableDependencySettings> {

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

    private class VulnerableMethodCollector extends CEDynamicMethodCollector {
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
                    vulnerableDependencies.add(result.dependencyName());
                }
            }

            if (!vulnerableDependencies.isEmpty()) {
                return vulnerableMethodInlay(vulnerableDependencies.size());
            }
            return null;
        }

        // text parsers
        protected InlayVisuals vulnerableMethodInlay(int vuln) {
            String tooltip = vuln == 1 ? CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.tooltip.singular") :
                    CEBundle.getString("inlay.vulnerabledependency.vulnerablemethod.tooltip.plural", vuln);

            return InlayVisuals.of(getSettings().getVulnerableMethod(), tooltip);
        }


        protected InlayVisuals indirectVulnerableMethodInlay() {
            String tooltip = CEBundle.getString("inlay.vulnerabledependency.indirectvulnerable.tooltip");
            return InlayVisuals.of(getSettings().getIndirectVulnerableMethod(), tooltip);
        }
    }

    private class VulnerableMethodReferenceCollector extends VulnerableMethodCollector {
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
            if (getSettings().isCheckVulnerableDependencyApplied()) {
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

    private class VulnerableDependencyCallCollector extends CEDynamicReferenceMethodCollector {
        protected VulnerableDependencyCallCollector(@NotNull Editor editor, String key) {
            super(editor, key);
        }

        @Override
        protected InlayVisuals createInlayFor(@NotNull PsiMethod method) {
            Project project = getEditor().getProject();
            if (CEUtils.checkMethodExternality(method, project) || true) {
                InlayInfo result = CEUtils.isVulnerable(method, project, getExternalInfo(method));
                result = new InlayInfo("skibid", Map.of("CRITICAL", 1), "scanner");
                if (result != null) {
                    return makeVulnerableDependencyCallInlay(result);
                }
            }

            return null;
        }

        private InlayVisuals makeVulnerableDependencyCallInlay(InlayInfo result) {
            StringBuilder severityBuilder = new StringBuilder();

            boolean firstSeverity = true;
            int totalVulnerabilities = 0;

            for (var entry : result.severityCounts().entrySet()) {
                int count = entry.getValue();
                InlayInfo.Severity severity = entry.getKey();
                if (count > 0) {
                    if (!firstSeverity) {
                        severityBuilder.append(", ");
                    }
                    severityBuilder.append(count).append(" ")
                            .append(CEBundle.getString("inlay.vulnerabledependency.call.severity." +
                                    severity.name().toLowerCase(Locale.ROOT)));
                    firstSeverity = false;
                    totalVulnerabilities += count;
                }
            }

            String vulnerabilitiesTooltip = totalVulnerabilities == 1 ?
                    CEBundle.getString("inlay.vulnerabledependency.tooltip.singular", severityBuilder.toString()) :
                    CEBundle.getString("inlay.vulnerabledependency.tooltip.plural", severityBuilder.toString());

            String scannerPrefix = CEBundle.getString("inlay.vulnerabledependency.call.scanner", result.scanner());
            return InlayVisuals.of(getSettings().getVulnerableDependencyCall(),
                    scannerPrefix + result.dependencyName() + " " + vulnerabilitiesTooltip);
        }

    }

}
