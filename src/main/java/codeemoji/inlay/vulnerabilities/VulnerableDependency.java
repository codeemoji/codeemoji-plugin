package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.external.DependencyInfo;
import codeemoji.inlay.external.VulnerabilityInfo;
import codeemoji.inlay.structuralanalysis.element.method.ExternalFunctionalityInvokingMethod;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static codeemoji.inlay.vulnerabilities.VulnerableDependencySymbols.*;

public class VulnerableDependency extends CEProviderMulti<VulnerableDependencySettings> {

    private static final Map<CESymbol, String> VULNERABILITY_THRESHOLDS = new HashMap<>();

    private final ExternalFunctionalityInvokingMethod externalFunctionalityChecker = new ExternalFunctionalityInvokingMethod();

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
        return Arrays.asList(
                createCollector(editor, ".low", VULNERABLE_LOW),
                createReferenceCollector(editor, ".low", VULNERABLE_LOW),
                createCollector(editor, ".medium", VULNERABLE_MEDIUM),
                createReferenceCollector(editor, ".medium", VULNERABLE_MEDIUM),
                createCollector(editor, ".high", VULNERABLE_HIGH),
                createReferenceCollector(editor, ".high", VULNERABLE_HIGH),
                createCollector(editor, ".critical", VULNERABLE_CRITICAL),
                createReferenceCollector(editor, ".critical", VULNERABLE_CRITICAL)
        );
    }

    private InlayHintsCollector createCollector(Editor editor, String suffix, CESymbol symbol) {
        return new CEMethodCollector(editor, getKeyId() + suffix, symbol) {
            @Override
            protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isInvokingMethodVulnerable(element, editor.getProject(), externalInfo, symbol);
            }
        };
    }

    private InlayHintsCollector createReferenceCollector(Editor editor, String suffix, CESymbol symbol) {
        return new CEReferenceMethodCollector(editor, getKeyId() + suffix, symbol) {
            @Override
            protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isInvokingMethodVulnerable(element, editor.getProject(), externalInfo, symbol);
            }
        };
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableDependencySettings settings) {
        return new VulnerableDependencyConfigurable(settings);
    }

    public boolean isInvokingMethodVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo, CESymbol threshold) {
        return isInvokingMethodVulnerable(method, project, externalInfo, threshold, new HashSet<>());
    }

    private boolean isInvokingMethodVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo, CESymbol threshold, Set<PsiMethod> visitedMethods) {
        if (visitedMethods.contains(method)) {
            return false; // We've already checked this method, so return false to avoid infinite recursion
        }
        visitedMethods.add(method);

        PsiMethod[] externalFunctionalityInvokingMethods = MethodAnalysisUtils.collectExternalFunctionalityInvokingMethods(method);

        if(
                externalFunctionalityInvokingMethods.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingMethods).anyMatch(externalFunctionalityInvokingMethod -> isVulnerable(externalFunctionalityInvokingMethod, project, externalInfo, threshold))
        ){
            return true;
        }

        else {

            if (getSettings().isCheckVulnerableDependecyApplied()){
                return Arrays.stream(externalFunctionalityInvokingMethods)
                        .filter(externalFunctionalityInvokingMethod -> !isVulnerable(externalFunctionalityInvokingMethod, project, externalInfo, threshold))
                        .anyMatch(externalFunctionalityInvokingMethod -> isInvokingMethodVulnerable(externalFunctionalityInvokingMethod, project, externalInfo, threshold, visitedMethods));
            }

            else{
                return  false;
            }
        }
    }

    private boolean isVulnerable(PsiMethod method, Project project, Map<?, ?> externalInfo, CESymbol threshold) {

        if (!MethodAnalysisUtils.checkMethodExternality(method, project)) {
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
