package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.CECollector;
import codeemoji.core.collector.DynamicInlayBuilder;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
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

    private CECollector collector;

    private static final Map<CESymbol, String> VULNERABILITY_THRESHOLDS = new HashMap<>();

    static {
        VULNERABILITY_THRESHOLDS.put(VULNERABLE_LOW, "LOW");
        VULNERABILITY_THRESHOLDS.put(VULNERABLE_MEDIUM, "MEDIUM");
        VULNERABILITY_THRESHOLDS.put(VULNERABLE_HIGH, "HIGH");
        VULNERABILITY_THRESHOLDS.put(VULNERABLE_CRITICAL, "CRITICAL");
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
                createCollector(editor, ".low", VULNERABLE_LOW),
                //createReferenceCollector(editor, ".low", VULNERABLE_LOW),
                createCollector(editor, ".medium", VULNERABLE_MEDIUM),
                //createReferenceCollector(editor, ".medium", VULNERABLE_MEDIUM),
                createCollector(editor, ".high", VULNERABLE_HIGH),
                //createReferenceCollector(editor, ".high", VULNERABLE_HIGH),
                createCollector(editor, ".critical", VULNERABLE_CRITICAL)
                //createReferenceCollector(editor, ".critical", VULNERABLE_CRITICAL)
        );
    }

    private InlayHintsCollector createCollector(Editor editor, String suffix, CESymbol symbol) {
        return new CEDynamicMethodCollector(editor) {
            @Override
            public InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isInvokingMethodVulnerable(element, editor.getProject(), externalInfo, symbol);
            }
        };
    }

    /*private InlayHintsCollector createReferenceCollector(Editor editor, String suffix, CESymbol symbol) {
        return new CEReferenceMethodCollector(editor, getKeyId() + suffix, symbol) {
            @Override
            protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isInvokingMethodVulnerable(element, editor.getProject(), externalInfo, symbol);
            }
        };
    }*/

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableDependencySettings settings) {
        return new VulnerableDependencyConfigurable(settings);
    }

    public InlayPresentation isInvokingMethodVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo, CESymbol threshold) {
        return isInvokingMethodVulnerable(method, project, externalInfo, threshold, new HashSet<>());
    }

    private InlayPresentation isInvokingMethodVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo, CESymbol threshold, Set<PsiMethod> visitedMethods) {
        if (visitedMethods.contains(method)) {
            return null; // We've already checked this method, so return false to avoid infinite recursion
        }
        visitedMethods.add(method);

        PsiMethod[] externalFunctionalityInvokingMethods = CEUtils.collectExternalFunctionalityInvokingMethods(method);

        if(
                externalFunctionalityInvokingMethods.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingMethods).anyMatch(externalFunctionalityInvokingMethod -> isVulnerable(externalFunctionalityInvokingMethod, project, externalInfo, threshold))
        ){
            return inlayBuilder.buildInlayWithEmoji(VULNERABLE_LOW, "inlay." + getKeyId() + ".reference.tooltip", null);
        }

        else {
            return null;
            /*if (getSettings().isCheckVulnerableDependecyApplied()){
                return Arrays.stream(externalFunctionalityInvokingMethods)
                        .filter(externalFunctionalityInvokingMethod -> !isVulnerable(externalFunctionalityInvokingMethod, project, externalInfo, threshold))
                        .anyMatch(externalFunctionalityInvokingMethod -> isInvokingMethodVulnerable(externalFunctionalityInvokingMethod, project, externalInfo, threshold, visitedMethods));
            }

            else{
                return  null;
            }*/
        }
    }

    private boolean isVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo, CESymbol threshold) {

        if (!CEUtils.checkMethodExternality(method, project)) {
            return false;
        }

        VirtualFile file = method.getNavigationElement().getContainingFile().getVirtualFile();
        if (file == null) {
            return false;
        }
        String normalizedPath = CEUtils.normalizeDependencyPath(file.getPath());

        String thresholdString = VULNERABILITY_THRESHOLDS.get(threshold);
        Iterator<? extends Map.Entry<?, ?>> iterator = externalInfo.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> entry = iterator.next();
            if (entry.getKey() instanceof DependencyInfo dependencyInfo) {
                String name = dependencyInfo.getPath();
                String[] nameParts = name.split("@");
                String dependency = nameParts[0];
                if (dependency.equals(normalizedPath)) {
                    if (entry.getValue() instanceof ArrayList<?> cveList) {
                        String maxSeverity = getMaxSeverity(cveList.stream()
                                .filter(VulnerabilityInfo.class::isInstance)
                                .map(VulnerabilityInfo.class::cast)
                                .collect(Collectors.toList()));
                        return maxSeverity.equals(thresholdString);
                    }
                }
            }
        }
        return false;
    }


    public String getMaxSeverity(List<VulnerabilityInfo> vulnerabilities) {
        String[] severityOrder = {"CRITICAL", "HIGH", "MEDIUM", "LOW"};

        for (String severity : severityOrder) {
            for (VulnerabilityInfo v : vulnerabilities) {
                if (v.getSeverity().equals(severity)) {
                    return severity;
                }
            }
        }
        return "ERROR";
    }
}
