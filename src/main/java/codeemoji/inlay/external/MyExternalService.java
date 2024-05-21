package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

@Service
@Getter
public final class MyExternalService implements CEExternalService<VirtualFile, Object> {

    Map<VirtualFile, Object> persistedData = new HashMap<>(); // is this really necessary?
    HashMap<Library, JSONArray> hashMap = new HashMap<>();

    List<String> librariesFromFiles = new ArrayList<>();
    Library[] librariesFromProject = null;

    DependencyParser parser = new DependencyParser();
    DependencyChecker checker = new DependencyChecker();

    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        getProjectLibraries(project);
        // PsiPackageStatement[] statements = getPsiPackageStatementsFromLibraries(project);

        for (Library lib : librariesFromProject) {
            String library = parser.parseDependencyToString(lib);
            JSONObject vulnerability = checker.checkDependency(library);
            // System.out.println("Risposta dall'API OSS Index (JSONArray):");
            try {
                String coordinates = vulnerability.getString("coordinates");
                JSONArray vulnerabilities = vulnerability.getJSONArray("vulnerabilities");
                if (vulnerabilities.length() != 0) {
                    hashMap.putIfAbsent(lib, vulnerabilities);
                } else {
                    // System.out.println(coordinates + " not added to hashmap because no vulnerabilities has been found");
                }
                System.out.println(hashMap);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public PsiPackageStatement[] getPsiPackageStatementsFromLibraries(Project project) {
        LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library[] librariesFromProject = libraryTable.getLibraries();
        Set<PsiPackageStatement> packageStatementsSet = new HashSet<>();

        for (Library library : librariesFromProject) {
            for (VirtualFile root : library.getFiles(OrderRootType.CLASSES)) {
                Set<PsiJavaFile> psiJavaFiles = getPsiJavaFilesFromRoot(project, root);
                for (PsiJavaFile psiJavaFile : psiJavaFiles) {
                    PsiPackageStatement packageStatement = psiJavaFile.getPackageStatement();
                    if (packageStatement != null) {
                        packageStatementsSet.add(packageStatement);
                    }
                }
            }
        }
        return packageStatementsSet.toArray(new PsiPackageStatement[0]);
    }

    private Set<PsiJavaFile> getPsiJavaFilesFromRoot(Project project, VirtualFile root) {
        Set<PsiJavaFile> psiJavaFiles = new HashSet<>();
        if (root.isDirectory()) {
            for (VirtualFile child : root.getChildren()) {
                psiJavaFiles.addAll(getPsiJavaFilesFromRoot(project, child));
            }
        } else if (root.getFileType().getDefaultExtension().equals("jar")) {
            // Handle jar files
            extractPsiJavaFilesFromJar(project, root, psiJavaFiles);
        } else if (root.getFileType().getDefaultExtension().equals("java")) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(root);
            if (psiFile instanceof PsiJavaFile) {
                psiJavaFiles.add((PsiJavaFile) psiFile);
            }
        }
        return psiJavaFiles;
    }

    private void extractPsiJavaFilesFromJar(Project project, VirtualFile jarFile, Set<PsiJavaFile> psiJavaFiles) {
        VirtualFile jarRoot = JarFileSystem.getInstance().getJarRootForLocalFile(jarFile);
        if (jarRoot != null) {
            for (VirtualFile file : jarRoot.getChildren()) {
                if (file.getFileType().getDefaultExtension().equals("java")) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    if (psiFile instanceof PsiJavaFile) {
                        psiJavaFiles.add((PsiJavaFile) psiFile);
                    }
                }
            }
        }
    }

    @Override
    public Map<VirtualFile, Object> getPersistedData() {
        return null;
    }

    public void getProjectLibraries(Project project) {
        LibraryTable librarytable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        librariesFromProject = librarytable.getLibraries();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        try {
            if (element != null) {
                // Retrieves preprocessed persistent values
                var data = retrieveData(element.getProject().getWorkspaceFile());
                // Put informations about element
                infoResult.putAll(hashMap);
            }
        } catch (RuntimeException ignored) {
        }
    }



}