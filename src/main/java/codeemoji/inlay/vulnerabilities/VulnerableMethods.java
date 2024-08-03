package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.external.DependencyInfo;
import codeemoji.inlay.external.VulnerabilityInfo;
import codeemoji.inlay.structuralanalysis.element.method.ExternalFunctionalityInvokingMethod;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static codeemoji.inlay.vulnerabilities.VulnerableSymbols.*;

public class VulnerableMethods extends CEProviderMulti<NoSettings> {

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
                        return isVulnerableExternalFunctionalityInvokingMethod(element, editor.getProject(), false, externalInfo, VULNERABLE_LOW);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".low", VULNERABLE_LOW) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isVulnerableExternalFunctionalityInvokingMethod(element, editor.getProject(), true, externalInfo, VULNERABLE_LOW);
                    }
                },
                new CEMethodCollector(editor, getKeyId() + ".medium", VULNERABLE_MEDIUM) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isVulnerableExternalFunctionalityInvokingMethod(element, editor.getProject(), false, externalInfo, VULNERABLE_MEDIUM);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".medium", VULNERABLE_MEDIUM) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isVulnerableExternalFunctionalityInvokingMethod(element, editor.getProject(), true, externalInfo, VULNERABLE_MEDIUM);
                    }
                },
                new CEMethodCollector(editor, getKeyId() + ".high", VULNERABLE_HIGH) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isVulnerableExternalFunctionalityInvokingMethod(element, editor.getProject(), false, externalInfo, VULNERABLE_HIGH);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".high", VULNERABLE_HIGH) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isVulnerableExternalFunctionalityInvokingMethod(element, editor.getProject(), true, externalInfo, VULNERABLE_HIGH);
                    }
                },
                new CEMethodCollector(editor, getKeyId() + ".critical", VULNERABLE_CRITICAL) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isVulnerableExternalFunctionalityInvokingMethod(element, editor.getProject(), false, externalInfo, VULNERABLE_CRITICAL);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".critical", VULNERABLE_CRITICAL) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isVulnerableExternalFunctionalityInvokingMethod(element, editor.getProject(), true, externalInfo, VULNERABLE_CRITICAL);
                    }
                }

        );
    }

    public boolean isVulnerableExternalFunctionalityInvokingMethod(PsiMethod method, Project project, boolean fromReferenceMethod, Map<?, ?> externalInfo, CESymbol threshold) {
        return externalFunctionalityChecker.isExternalFunctionalityInvokingMethod(
                method,
                project,
                fromReferenceMethod,
                calledMethod -> isVulnerable(calledMethod, externalInfo, threshold)
        );
    }

    private boolean isVulnerable(PsiMethod method, Map<?, ?> externalInfo, CESymbol threshold) {

        VirtualFile file = method.getNavigationElement().getContainingFile().getVirtualFile();
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
    // TODO optimize
    public String getMaxSeverity(ArrayList<VulnerabilityInfo> vulnerabilities) {
        for (VulnerabilityInfo v : vulnerabilities) {
            if (v.getSeverity().equals("CRITICAL")) {
                return v.getSeverity();
            }
        }
        for (VulnerabilityInfo v : vulnerabilities) {
            if (v.getSeverity().equals("HIGH")) {
                return v.getSeverity();
            }
        }
        for (VulnerabilityInfo v : vulnerabilities) {
            if (v.getSeverity().equals("MEDIUM")) {
                return v.getSeverity();
            }
        }
        for (VulnerabilityInfo v : vulnerabilities) {
            if (v.getSeverity().equals("LOW")) {
                return v.getSeverity();
            }
        }
        return "ERROR";
    }
}
