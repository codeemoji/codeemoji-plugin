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
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return List.of(
                new CEMethodCollector(editor, getKeyId() + ".low", VULNERABLE_LOW) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isInvokingMethodVulnerable(element, editor.getProject(), false, externalInfo, VULNERABLE_LOW);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".low", VULNERABLE_LOW) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isInvokingMethodVulnerable(element, editor.getProject(), true, externalInfo, VULNERABLE_LOW);
                    }
                },
                new CEMethodCollector(editor, getKeyId() + ".medium", VULNERABLE_MEDIUM) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isInvokingMethodVulnerable(element, editor.getProject(), false, externalInfo, VULNERABLE_MEDIUM);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".medium", VULNERABLE_MEDIUM) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isInvokingMethodVulnerable(element, editor.getProject(), true, externalInfo, VULNERABLE_MEDIUM);
                    }
                },
                new CEMethodCollector(editor, getKeyId() + ".high", VULNERABLE_HIGH) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isInvokingMethodVulnerable(element, editor.getProject(), false, externalInfo, VULNERABLE_HIGH);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".high", VULNERABLE_HIGH) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isInvokingMethodVulnerable(element, editor.getProject(), true, externalInfo, VULNERABLE_HIGH);
                    }
                },
                new CEMethodCollector(editor, getKeyId() + ".critical", VULNERABLE_CRITICAL) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isInvokingMethodVulnerable(element, editor.getProject(), false, externalInfo, VULNERABLE_CRITICAL);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".critical", VULNERABLE_CRITICAL) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isInvokingMethodVulnerable(element, editor.getProject(), true, externalInfo, VULNERABLE_CRITICAL);
                    }
                }

        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableDependencySettings settings) {
        return new VulnerableDependencyConfigurable(settings);
    }

    public boolean isInvokingMethodVulnerable(PsiMethod method, Project project, boolean fromReferenceMethod, Map<?, ?> externalInfo, CESymbol threshold) {
        return isInvokingMethodVulnerable(method, project, fromReferenceMethod, externalInfo, new HashSet<>(), threshold);
    }

    private boolean isInvokingMethodVulnerable(PsiMethod method, Project project, boolean fromReferenceMethod, Map<?, ?> externalInfo, Set<PsiMethod> visitedMethods, CESymbol threshold) {
        if (visitedMethods.contains(method)) {
            return false; // We've already checked this method, so return false to avoid infinite recursion
        }
        visitedMethods.add(method);

        PsiMethod[] externalFunctionalityInvokingMethods = MethodAnalysisUtils.collectExternalFunctionalityInvokingMethods(method);

        if(
                externalFunctionalityInvokingMethods.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingMethods).anyMatch(externalFunctionalityInvokingMethod -> isVulnerable(externalFunctionalityInvokingMethod, externalInfo, threshold))
        ){
            return true;
        }

        else {

            if (getSettings().isCheckVulnerableDependecyApplied()){
                return Arrays.stream(externalFunctionalityInvokingMethods)
                        .filter(externalFunctionalityInvokingMethod -> !MethodAnalysisUtils.checkMethodExternality(externalFunctionalityInvokingMethod, project))
                        .anyMatch(externalFunctionalityInvokingMethod -> isInvokingMethodVulnerable(externalFunctionalityInvokingMethod, project, fromReferenceMethod, externalInfo, visitedMethods, threshold));
            }

            else{
                return  false;
            }
        }
    }


    private boolean isVulnerable(PsiMethod method, Map<?, ?> externalInfo, CESymbol threshold) {

        VirtualFile file = method.getNavigationElement().getContainingFile().getVirtualFile();
        if (file == null) {
            return false;
        }
        String path = normalizePath(file.getPath());
        for (Map.Entry<?, ?> entry : externalInfo.entrySet()) {
            if (entry.getKey() instanceof DependencyInfo dependencyInfo) {
                String name = dependencyInfo.getPath();
                String[] nameParts = name.split("@");
                String dependency = nameParts[0];
                if (dependency.equals(path)) {
                    String thresholdString = VULNERABILITY_THRESHOLDS.get(threshold);
                    ArrayList<VulnerabilityInfo> cveList = (ArrayList<VulnerabilityInfo>) entry.getValue();
                    String maxSeverity = getMaxSeverity(cveList);
                    return maxSeverity.equals(thresholdString);
                }
            }
        }

        return false;
    }

    private String normalizePath(String path) {
        Pattern pattern = Pattern.compile(".*/modules-2/([^/]+)/([^/]+)/([^/]+)/([^/]+)/.*");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(2) + "/" + matcher.group(3);  // Return group/artifact
        }
        return path;
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
