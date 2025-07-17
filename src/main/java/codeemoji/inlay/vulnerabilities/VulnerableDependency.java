package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.base.CEMethodCollector;
import codeemoji.core.collector.base.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
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

public class VulnerableDependency extends CEProvider<VulnerableDependencySettings> {

    @Override
    public @NotNull CEBaseConfigurableWindow<VulnerableDependencySettings> createConfigurable() {
        return new VulnerableDependencyConfigurable();
    }

    @Override
    protected void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        String key = getKey();
        builder.add(new VulnerableMethodCollector(editor, key));
        builder.add(new VulnerableMethodReferenceCollector(editor, key));
        if (getSettings().isCheckVulnerableDependencyApplied()) {
            builder.add(new IndirectVulnerableMethodCollector(editor, key));
        }
        builder.add(new VulnerableDependencyCallCollector(editor, key));
    }

    private class VulnerableMethodCollector extends CEMethodCollector {
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
            if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(method)) {
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

            return InlayVisuals.direct(getSettings().getVulnerableMethod(), tooltip);
        }


        protected InlayVisuals indirectVulnerableMethodInlay() {
            return InlayVisuals.translated(getSettings().getIndirectVulnerableMethod(),
                    "inlay.vulnerabledependency.indirectvulnerable.tooltip");
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

    private class VulnerableDependencyCallCollector extends CEReferenceMethodCollector {
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
            StringBuilder severityBuilder = new StringBuilder();

            boolean firstSeverity = true;
            int totalVulnerabilities = 0;

            for (var entry : result.severityCounts().entrySet()) {
                int count = entry.getValue();
                Severity severity = entry.getKey();
                if (count > 0) {
                    if (!firstSeverity) {
                        severityBuilder.append(", ");
                    }
                    severityBuilder.append(" ")
                            .append(CEBundle.getString("inlay.vulnerabledependency.call.severity." +
                                    severity.name().toLowerCase(Locale.ROOT), count));
                    firstSeverity = false;
                    totalVulnerabilities += count;
                }
            }

            String vulnerabilitiesTooltip = totalVulnerabilities == 1 ?
                    CEBundle.getString("inlay.vulnerabledependency.call.singular", result.dependencyName(), severityBuilder.toString()) :
                    CEBundle.getString("inlay.vulnerabledependency.call.plural", result.dependencyName(), severityBuilder.toString());

            String scannerPrefix = CEBundle.getString("inlay.vulnerabledependency.call.scanner", result.scanner());
            return InlayVisuals.direct(getSettings().getVulnerableDependencyCall(),
                    scannerPrefix + ": " + vulnerabilitiesTooltip);
        }

    }

}
