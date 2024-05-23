package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
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
                new CEMethodCollector(editor, getKeyId(), VULNERABLE_LOW) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_LOW, externalInfo, new HashSet<PsiMethod>());
                    }
                },
                new CEMethodCollector(editor, getKeyId(), VULNERABLE_MEDIUM) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_MEDIUM, externalInfo, new HashSet<PsiMethod>());
                    }
                },
                new CEMethodCollector(editor, getKeyId(), VULNERABLE_HIGH) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_HIGH, externalInfo, new HashSet<PsiMethod>());
                    }
                }

        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableMethodsSettings settings) {
        return new VulnerableMethodsConfigurable(settings);
    }

    public boolean isExternalFunctionalityVulnerable(PsiMethod method, Project project, CESymbol threshold, Map<?, ?> externalInfo, Set<PsiMethod> visitedMethods) {
        // to avoid infinite loop if the method was already visited
        if (!visitedMethods.add(method) || !checkMethodExternality(method)) {
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
                    if (isExternalFunctionalityVulnerable(resolvedMethod, project, threshold, externalInfo, visitedMethods)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkVulnerability(PsiMethod method, Map<?, ?> externalInfo, CESymbol threshold) {
        VirtualFile virtualFile = method.getContainingFile().getVirtualFile();
        if (virtualFile == null) {
            return false;
        }

        String methodFilePathNormalized = normalizePath(virtualFile.getPath());
        for (Object key : externalInfo.keySet()) {
            if (key instanceof Library library) {
                if (libraryContainsMethod(library, methodFilePathNormalized)) {
                    Object value = externalInfo.get(key);
                    if (value instanceof JSONObject jsonObject) {
                        JSONArray vulnerabilities = jsonObject.getJSONArray("vulnerabilities");
                        double maxCvssScore = getCvssScore(vulnerabilities );
                        return isEnoughVulnerable(maxCvssScore, threshold);
                    }
                    return false;
                }
            }
        }
        return false;
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

    public boolean checkMethodExternality(PsiMethod method) {
        return method.getContainingFile() instanceof PsiJavaFile javaFile &&
                method.getContainingClass() != null &&
                javaFile.getPackageStatement() != null &&
                !javaFile.getPackageName().startsWith("java");
                // && !CEUtils.getSourceRootsInProject(project).contains(ProjectFileIndex.getInstance(method.getProject()).getSourceRootForFile(method.getNavigationElement().getContainingFile().getVirtualFile()));
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

