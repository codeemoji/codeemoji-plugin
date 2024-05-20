package codeemoji.inlay.vulnerabilities;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.structuralanalysis.element.method.ExternalFunctionalityInvokingMethodSettings;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static codeemoji.inlay.vulnerabilities.VulnerableSymbols.VULNERABLE_LOW;



@SuppressWarnings("UnstableApiUsage")
public class VunerableMethods extends CEProviderMulti<ExternalFunctionalityInvokingMethodSettings> {

    @Override
    public String getPreviewText() {
        return """
                Its vulnerable""";
    }

    @Override
    protected @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        return List.of(
                new CEMethodCollector(editor, getKeyId(), VULNERABLE_LOW) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityInvokingMethod(element, editor.getProject(), false, externalInfo);
                    }
                },
                new CEReferenceMethodCollector(editor, getKeyId(), VULNERABLE_LOW) {
                    @Override
                    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isExternalFunctionalityInvokingMethod(element, editor.getProject(), true, externalInfo);
                    }
                }
        );
    }

    public boolean isExternalFunctionalityInvokingMethod(PsiMethod method, Project project, boolean fromReferenceMethod, Map<?, ?> externalInfo) {
        if (fromReferenceMethod && !checkMethodExternality(method, project)) {
            return false;
        }

        PsiJavaFile file = (PsiJavaFile) method.getContainingFile();
        PsiClass clas = method.getContainingClass();
        PsiPackageStatement statement = file.getPackageStatement();
        String st = statement.getPackageName();
        List<VirtualFile> listRoot = CEUtils.getSourceRootsInProject(project);
        VirtualFile virtualFile = ProjectFileIndex.getInstance(method.getProject()).getSourceRootForFile(method.getNavigationElement().getContainingFile().getVirtualFile());

        PsiElement[] externalFunctionalityInvokingElements = PsiTreeUtil.collectElements(
                method.getNavigationElement(),
                externalFunctionalityInvokingElement ->
                        externalFunctionalityInvokingElement instanceof PsiMethodCallExpression methodCallExpression &&
                                methodCallExpression.resolveMethod() != null && !method.isEquivalentTo(methodCallExpression.resolveMethod())
        );

        if (externalFunctionalityInvokingElements.length > 0 &&
                Arrays.stream(externalFunctionalityInvokingElements)
                        .map(externalFunctionalityInvokingElement -> ((PsiMethodCallExpression) externalFunctionalityInvokingElement.getNavigationElement()).resolveMethod())
                        .anyMatch(externalFunctionalityInvokingElement -> {
                            if (checkMethodExternality(externalFunctionalityInvokingElement, project)) {
                                return checkVulnerability(externalFunctionalityInvokingElement, externalInfo);
                            }
                            return false;
                        })
        ) {
            return true;
        } else {
            if (getSettings().isCheckMethodCallsForExternalityApplied()) {
                return externalFunctionalityInvokingElements.length > 0 &&
                        Arrays.stream(externalFunctionalityInvokingElements)
                                .map(externalFunctionalityInvokingElement -> ((PsiMethodCallExpression) externalFunctionalityInvokingElement).resolveMethod())
                                .filter(externalFunctionalityInvokingElement -> !checkMethodExternality((PsiMethod) externalFunctionalityInvokingElement.getNavigationElement(), project))
                                .anyMatch(externalFunctionalityInvokingElement -> isExternalFunctionalityInvokingMethod(externalFunctionalityInvokingElement, project, fromReferenceMethod, externalInfo));
            } else {
                return false;
            }
        }
    }


    public boolean checkMethodExternality(PsiMethod method, Project project) {
        return method.getContainingFile() instanceof PsiJavaFile javaFile &&
                method.getContainingClass() != null &&
                javaFile.getPackageStatement() != null &&
                !javaFile.getPackageName().startsWith("java") &&
                !CEUtils.getSourceRootsInProject(project).contains(ProjectFileIndex.getInstance(method.getProject()).getSourceRootForFile(method.getNavigationElement().getContainingFile().getVirtualFile()));
    }

    public boolean checkVulnerability(PsiMethod method, Map<?, ?> externalInfo) {
        double maxCvssScore = Double.MIN_VALUE; // Inizializza il massimo punteggio CVSS a un valore molto basso
        String qualifiedName = method.getContainingClass().getQualifiedName();
        if (qualifiedName == null) {
            return false;
        }
        for (Object key : externalInfo.keySet()) {
            if (key instanceof Library) {
                Library library = (Library) key;

                String libraryName = library.getName();
                // String[] urls = library.getUrls(OrderRootType.CLASSES);
                String[] parts = libraryName.split(":");
                String group = parts[1];
                String artifact = parts[2];

                if (qualifiedName.contains(group) || qualifiedName.contains(artifact)) {
                    Object value = externalInfo.get(key);
                    if (value instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) value;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            double cvssScore = jsonObject.getDouble("cvssScore");
                            if (cvssScore > maxCvssScore) {
                                maxCvssScore = cvssScore;
                            }
                        }
                        System.out.println("Vulnerability MAX SCORE: " + maxCvssScore);
                    }
                    return true;
                }
            }
        }

        return false;
    }

    /*public boolean checkVulnerability(@NotNull PsiMethod method, Map<?, ?> externalInfo, @NotNull Project project) {
        PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return false;
        }

        for (Object key : externalInfo.keySet()) {
            if (key instanceof Library) {
                Library library = (Library) key;

                // Retrieve all classes in the library
                Set<PsiClass> libraryClasses = getAllLibraryClasses(library, project);
                for (PsiClass libraryClass : libraryClasses) {
                    if (libraryClass.isEquivalentTo(containingClass)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Set<PsiClass> getAllLibraryClasses(Library library, Project project) {
        Set<PsiClass> classes = new HashSet<>();
        for (VirtualFile root : library.getFiles(OrderRootType.CLASSES)) {
            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(root);
            if (directory != null) {
                collectClasses(directory, classes);
            }
        }
        return classes;
    }

    private void collectClasses(PsiDirectory directory, Set<PsiClass> classes) {
        for (PsiFile file : directory.getFiles()) {
            if (file instanceof PsiClassOwner) {
                for (PsiClass psiClass : ((PsiClassOwner) file).getClasses()) {
                    classes.add(psiClass);
                }
            }
        }
        for (PsiDirectory subDirectory : directory.getSubdirectories()) {
            collectClasses(subDirectory, classes);
        }
    }*/

}

