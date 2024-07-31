package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.external.VulnerabilityInfo;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static codeemoji.inlay.vulnerabilities.VulnerableSymbols.*;

@SuppressWarnings("UnstableApiUsage")
public class VunerableMethods extends CEProviderMulti<VulnerableMethodsSettings> {

    private static final Map<CESymbol, Double[]> VULNERABILITY_THRESHOLDS = new HashMap<>();

    static {
        VULNERABILITY_THRESHOLDS.put(VULNERABLE_LOW, new Double[]{0.0, 5.0});
        VULNERABILITY_THRESHOLDS.put(VULNERABLE_MEDIUM, new Double[]{5.0, 7.5});
        VULNERABILITY_THRESHOLDS.put(VULNERABLE_HIGH, new Double[]{7.5, 10.0});
    }

    @Override
    public String getPreviewText() {
        return """
                """;
    }

    @Override
    protected @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        return List.of(
                new CEMethodCollector(editor, getKeyId() + ".low", VULNERABLE_LOW) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        if (externalInfo instanceof Map<?, ?> && allEntriesMatch(externalInfo)) {
                            @SuppressWarnings("unchecked")
                            Map<JSONObject, List<VulnerabilityInfo>> castedInfo = (Map<JSONObject, List<VulnerabilityInfo>>) externalInfo;
                            return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_LOW, castedInfo, new HashSet<>(), false);
                        }
                        return false;
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".low", VULNERABLE_LOW) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        if (externalInfo instanceof Map<?, ?> && allEntriesMatch(externalInfo)) {
                            @SuppressWarnings("unchecked")
                            Map<JSONObject, List<VulnerabilityInfo>> castedInfo = (Map<JSONObject, List<VulnerabilityInfo>>) externalInfo;
                            return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_LOW, castedInfo, new HashSet<>(), true);
                        }
                        return false;
                    }
                },
                new CEMethodCollector(editor, getKeyId() + ".medium", VULNERABLE_MEDIUM) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        if (externalInfo instanceof Map<?, ?> && allEntriesMatch(externalInfo)) {
                            @SuppressWarnings("unchecked")
                            Map<JSONObject, List<VulnerabilityInfo>> castedInfo = (Map<JSONObject, List<VulnerabilityInfo>>) externalInfo;
                            return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_MEDIUM, castedInfo, new HashSet<>(), false);
                        }
                        return false;
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".medium", VULNERABLE_MEDIUM) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        if (externalInfo instanceof Map<?, ?> && allEntriesMatch(externalInfo)) {
                            @SuppressWarnings("unchecked")
                            Map<JSONObject, List<VulnerabilityInfo>> castedInfo = (Map<JSONObject, List<VulnerabilityInfo>>) externalInfo;
                            return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_MEDIUM, castedInfo, new HashSet<>(), true);
                        }
                        return false;
                    }
                },
                new CEMethodCollector(editor, getKeyId() + ".high", VULNERABLE_HIGH) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        if (externalInfo instanceof Map<?, ?> && allEntriesMatch(externalInfo)) {
                            @SuppressWarnings("unchecked")
                            Map<JSONObject, List<VulnerabilityInfo>> castedInfo = (Map<JSONObject, List<VulnerabilityInfo>>) externalInfo;
                            return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_HIGH, castedInfo, new HashSet<>(), false);
                        }
                        return false;
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId() + ".high", VULNERABLE_HIGH) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        if (externalInfo instanceof Map<?, ?> && allEntriesMatch(externalInfo)) {
                            @SuppressWarnings("unchecked")
                            Map<JSONObject, List<VulnerabilityInfo>> castedInfo = (Map<JSONObject, List<VulnerabilityInfo>>) externalInfo;
                            return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_HIGH, castedInfo, new HashSet<>(), true);
                        }
                        return false;
                    }
                });
    }

    private boolean allEntriesMatch(Map<?, ?> map) {
        return map.entrySet().stream().allMatch(entry ->
                entry.getKey() instanceof JSONObject &&
                        entry.getValue() instanceof List &&
                        ((List<?>) entry.getValue()).stream().allMatch(item -> item instanceof VulnerabilityInfo)
        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableMethodsSettings settings) {
        return new VulnerableMethodsConfigurable(settings);
    }


    public boolean isExternalFunctionalityVulnerable(PsiMethod method, Project project, CESymbol threshold, Map<JSONObject,
            List<VulnerabilityInfo>> externalInfo, Set<PsiMethod> visitedMethods, boolean fromReferenceMethod) {
        if(fromReferenceMethod && !checkMethodExternality(method, project)) {
            return false;
        }

        // to avoid infinite loop if the method was already visited
        if (!visitedMethods.add(method)) {
            return false;
        }
        PsiElement[] vulnerabilityElements = PsiTreeUtil.collectElements(
                method.getNavigationElement(),
                vulElement -> vulElement instanceof PsiMethodCallExpression
                        && ((PsiMethodCallExpression) vulElement).resolveMethod() != null
                        && !method.isEquivalentTo(((PsiMethodCallExpression) vulElement).resolveMethod())
        );

        for (PsiElement vulElement : vulnerabilityElements) {
            PsiMethod resolvedMethod = ((PsiMethodCallExpression) vulElement).resolveMethod();
            if (resolvedMethod != null && checkVulnerability(resolvedMethod, externalInfo, threshold)) {
                return true;
            }
        }

        // if we need to search recursively
        if (getSettings().isCheckMethodCallsForExternalityApplied()) {
            for (PsiElement vulnerabilityElement : vulnerabilityElements) {
                PsiMethod resolvedMethod = ((PsiMethodCallExpression) vulnerabilityElement).resolveMethod();
                if (resolvedMethod != null && !checkVulnerability(resolvedMethod, externalInfo, threshold)) {
                    if (isExternalFunctionalityVulnerable(resolvedMethod, project, threshold, externalInfo, visitedMethods, fromReferenceMethod)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkVulnerability(PsiMethod method, Map<JSONObject, List<VulnerabilityInfo>> externalInfo, CESymbol threshold) {
        VirtualFile virtualFile = method.getContainingFile().getVirtualFile();
        if (virtualFile == null) {
            return false;
        }

        String methodFilePathNormalized = normalizePath(virtualFile.getPath());
        for (Map.Entry<JSONObject, List<VulnerabilityInfo>> entry : externalInfo.entrySet()) {
            JSONObject dependency = entry.getKey();
            String dependencyName = dependency.getString("name"); // Assuming the JSONObject has a "name" field
            if (methodFilePathNormalized.contains(dependencyName)) {
                List<VulnerabilityInfo> vulnerabilities = entry.getValue();
                double maxCvssScore = getMaxCvssScore(vulnerabilities);
                return isEnoughVulnerable(maxCvssScore, threshold);
            }
        }
        return false;
    }

    private double getMaxCvssScore(List<VulnerabilityInfo> vulnerabilities) {
        return vulnerabilities.stream()
                .mapToDouble(this::getCvssScoreFromSeverity)
                .max()
                .orElse(Double.MIN_VALUE);
    }

    private double getCvssScoreFromSeverity(VulnerabilityInfo vulnerabilityInfo) {
        return switch (vulnerabilityInfo.getSeverity().toUpperCase()) {
            case "CRITICAL" -> 9.5;
            case "HIGH" -> 8.0;
            case "MEDIUM" -> 6.0;
            case "LOW" -> 3.0;
            default -> 0.0;
        };
    }

    private boolean libraryContainsMethod(Library library, String methodFilePathNormalized) {
        for (String url : library.getUrls(OrderRootType.CLASSES)) {
            String libraryPathNormalized = normalizePath(url);
            if (methodFilePathNormalized.contains(libraryPathNormalized)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnoughVulnerable(double maxCvssScore, CESymbol threshold) {
        Double[] range = VULNERABILITY_THRESHOLDS.get(threshold);
        return range != null && maxCvssScore > range[0] && maxCvssScore <= range[1];
    }

    public boolean checkMethodExternality(PsiMethod method, Project project) {
        return method.getContainingFile() instanceof PsiJavaFile javaFile &&
                method.getContainingClass() != null &&
                javaFile.getPackageStatement() != null &&
                !javaFile.getPackageName().startsWith("java")
                && !CEUtils.getSourceRootsInProject(project).contains(ProjectFileIndex.getInstance(method.getProject()).getSourceRootForFile(method.getNavigationElement().getContainingFile().getVirtualFile()));
    }

    private double getCvssScore(JSONArray jsonArray) {
        return jsonArray.toList().stream()
                .mapToDouble(obj -> ((Map<String, Object>) obj).get("cvssScore") instanceof Number
                        ? ((Number) ((Map<String, Object>) obj).get("cvssScore")).doubleValue()
                        : Double.MIN_VALUE)
                .max().orElse(Double.MIN_VALUE);
    }

    private String normalizePath(String path) {
        Pattern pattern = Pattern.compile(".*/modules-2/([^/]+)/([^/]+)/([^/]+)/([^/]+)/.*");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return matcher.group(2) + "/" + matcher.group(3);  // Return group/artifact
        }
        return path;
    }
}

