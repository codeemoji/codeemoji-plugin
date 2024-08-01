package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.external.DependencyInfo;
import codeemoji.inlay.external.NistVulnerabilityScanner;
import codeemoji.inlay.external.VulnerabilityInfo;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
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

import static codeemoji.inlay.vulnerabilities.VulnerableSymbols.*;

public class VulnerableMethods extends CEProviderMulti<NoSettings> {

    private static final Map<CESymbol, String> VULNERABILITY_THRESHOLDS = new HashMap<>();

    private NistVulnerabilityScanner nistVulnerabilityScanner = new NistVulnerabilityScanner("", "");
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
        return isVulnerableExternalFunctionalityInvokingMethod(method, project, fromReferenceMethod, externalInfo, new HashSet<>(), threshold);
    }

    private boolean isVulnerableExternalFunctionalityInvokingMethod(PsiMethod method, Project project, boolean fromReferenceMethod, Map<?, ?> externalInfo, Set<PsiMethod> visitedMethods, CESymbol threshold) {
        if (visitedMethods.contains(method)) {
            return false; // We've already checked this method, avoid recursion
        }
        visitedMethods.add(method);

        if (fromReferenceMethod && !checkMethodExternality(method, project)) {
            return false;
        }

        PsiElement[] externalFunctionalityInvokingElements = PsiTreeUtil.collectElements(
                method.getNavigationElement(),
                element -> element instanceof PsiMethodCallExpression methodCallExpression &&
                        methodCallExpression.resolveMethod() != null &&
                        !method.isEquivalentTo(methodCallExpression.resolveMethod())
        );

        for (PsiElement element : externalFunctionalityInvokingElements) {
            PsiMethod calledMethod = ((PsiMethodCallExpression) element).resolveMethod();
            if (calledMethod != null && checkMethodExternality(calledMethod, project)) {
                if (isVulnerable(calledMethod, externalInfo, threshold)) {
                    return true;
                }
            }
        }
        if (true) {
        // if (getSettings().isCheckMethodCallsForExternalityApplied()) {
            for (PsiElement element : externalFunctionalityInvokingElements) {
                PsiMethod calledMethod = ((PsiMethodCallExpression) element).resolveMethod();
                if (calledMethod != null && !checkMethodExternality(calledMethod, project)) {
                    if (isVulnerableExternalFunctionalityInvokingMethod(calledMethod, project, fromReferenceMethod, externalInfo, visitedMethods, threshold)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkMethodExternality(PsiMethod method, Project project) {
        return method.getContainingFile() instanceof PsiJavaFile javaFile &&
                method.getContainingClass() != null &&
                javaFile.getPackageStatement() != null &&
                !javaFile.getPackageName().startsWith("java") &&
                !CEUtils.getSourceRootsInProject(project).contains(
                        ProjectFileIndex.getInstance(method.getProject()).getSourceRootForFile(
                                method.getNavigationElement().getContainingFile().getVirtualFile()
                        )
                );
    }

    private boolean isVulnerable(PsiMethod method, Map<?, ?> externalInfo, CESymbol threshold) {

        VirtualFile file = method.getNavigationElement().getContainingFile().getVirtualFile();
        String path = normalizePath(file.getPath());
        for (Map.Entry<?, ?> entry : externalInfo.entrySet()) {
            if (entry.getKey() instanceof DependencyInfo dependencyInfo) {
                String name = dependencyInfo.getName();
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
