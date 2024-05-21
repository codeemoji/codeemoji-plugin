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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static codeemoji.inlay.vulnerabilities.VulnerableSymbols.*;


@SuppressWarnings("UnstableApiUsage")
public class VunerableMethods extends CEProviderMulti<VulnerableMethodsSettings> {

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
                        return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_LOW, externalInfo);
                    }
                },
                new CEMethodCollector(editor, getKeyId(), VULNERABLE_MEDIUM) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_MEDIUM, externalInfo);
                    }
                },
                new CEMethodCollector(editor, getKeyId(), VULNERABLE_HIGH) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityVulnerable(element, editor.getProject(), VULNERABLE_HIGH, externalInfo);
                    }
                }

        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull VulnerableMethodsSettings settings) {
        return new VulnerableMethodsConfigurable(settings);
    }

    public boolean isExternalFunctionalityVulnerable(PsiMethod method, Project project, CESymbol treshold, Map<?, ?> externalInfo) {

        PsiElement[] externalFunctionalityInvokingElements = PsiTreeUtil.collectElements(
                method.getNavigationElement(),
                externalFunctionalityInvokingElement ->
                        externalFunctionalityInvokingElement instanceof PsiMethodCallExpression methodCallExpression &&
                                methodCallExpression.resolveMethod() != null && !method.isEquivalentTo(methodCallExpression.resolveMethod())
        );

        if (externalFunctionalityInvokingElements.length > 0 &&
                Arrays.stream(externalFunctionalityInvokingElements)
                        .map(externalFunctionalityInvokingElement -> ((PsiMethodCallExpression) externalFunctionalityInvokingElement.getNavigationElement()).resolveMethod())
                        .anyMatch(externalFunctionalityInvokingElement -> checkVulnerability(externalFunctionalityInvokingElement, externalInfo, treshold))
        ) {
            return true;
        } else {
            if (getSettings().isCheckMethodCallsForExternalityApplied()) {
                return externalFunctionalityInvokingElements.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingElements)
                                .map(externalFunctionalityInvokingElement -> ((PsiMethodCallExpression) externalFunctionalityInvokingElement).resolveMethod())
                                .filter(externalFunctionalityInvokingElement -> !checkVulnerability((PsiMethod) externalFunctionalityInvokingElement.getNavigationElement(), externalInfo, treshold))
                                .anyMatch(externalFunctionalityInvokingElement -> isExternalFunctionalityVulnerable(externalFunctionalityInvokingElement, project, treshold, externalInfo));
            } else {
                return false;
            }
        }
    }


    public boolean checkVulnerability(PsiMethod method, Map<?, ?> externalInfo, CESymbol treshold) {
        double maxCvssScore = Double.MIN_VALUE;
        PsiFile containingFile = method.getContainingFile();
        if (containingFile == null) {
            return false;
        }
        VirtualFile virtualFile = containingFile.getVirtualFile();
        if (virtualFile == null) {
            return false;
        }
        String methodFilePath = virtualFile.getPath();
        String methodFilePathNormalized = normalizePath(methodFilePath);

        for (Object key : externalInfo.keySet()) {
            if (key instanceof Library) {
                Library library = (Library) key;
                for (String url : library.getUrls(OrderRootType.CLASSES)) {
                    String libraryPathNormalized = normalizePath(url);
                    if (methodFilePathNormalized.contains(libraryPathNormalized)) {

                        Object value = externalInfo.get(key);
                        if (value instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray) value;
                            maxCvssScore = getCvssScore(jsonArray);
                            return isEnoughVulnerable(maxCvssScore, treshold);

                        } else {
                            return false;
                        }

                    }
                }
            }
        }

        return false;
    }

    private boolean isEnoughVulnerable(double maxCvssScore, CESymbol treshold) {
        if (treshold.equals(VULNERABLE_LOW)) {
            if (maxCvssScore > 0.0 && maxCvssScore <= 5) {
                return true;
            } else {
                return false;
            }
        } else if (treshold.equals(VULNERABLE_MEDIUM)) {
            if (maxCvssScore > 5 && maxCvssScore <= 7.5) {
                return true;
            } else {
                return false;
            }
        } else if (treshold.equals(VULNERABLE_HIGH)) {
            if (maxCvssScore > 7.5 && maxCvssScore <= 10.0) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private double getCvssScore(JSONArray jsonArray) {
        double maxCvssScore = Double.MIN_VALUE;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            double cvssScore = jsonObject.getDouble("cvssScore");
            if (cvssScore > maxCvssScore) {
                maxCvssScore = cvssScore;
            }
        }
        return maxCvssScore;
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

