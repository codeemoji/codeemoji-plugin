package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
public final class MyExternalService implements CEExternalService<VirtualFile, Object> {

    private final String API_BASE_URL = "https://api.whitesourcesoftware.com/";
    Map<VirtualFile, Object> persistedData = new HashMap<>();
    List<String> librariesFromFiles = new ArrayList<>();
    Library[] librariesFromProject = null;
    List<String> dependencies = new ArrayList<>();
    HashMap<Library, JSONArray> hashMap = new HashMap<>();
    DependencyParser parser = new DependencyParser();
    DependencyChecker checker = new DependencyChecker();

    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        getProjectLibraries(project);

        for (Library lib : librariesFromProject) {
            String library = parser.parseDependencyToString(lib);
            JSONObject vulnerability = checker.checkDependency(library);
            // System.out.println("Risposta dall'API OSS Index (JSONArray):");

            try {
                String coordinates = vulnerability.getString("coordinates");
                JSONArray vulnerabilities = vulnerability.getJSONArray("vulnerabilities");

                // Aggiungi all'HashMap solo se il JSONArray vulnerabilities non è vuoto
                if (vulnerabilities.length() != 0) {
                    hashMap.putIfAbsent(lib, vulnerabilities);
                } else {
                    // System.out.println(coordinates + " not added to hashmap because no vulnerabilities has been found");
                }

                // Stampa la HashMap
                System.out.println(hashMap);
            } catch (JSONException e) {
                throw new RuntimeException(e);
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
            // Per ogni libreria nella lista libraries, richiama retrieveVulnerabilitiesInfo per ottenere le informazioni sulle vulnerabilità
//            for (String libraryName : libraries) {
//                retrieveVulnerabilitiesInfo(libraryName, infoResult);
//            }
        } catch (RuntimeException ignored) {
        }
    }


    // FROM HERE ON JUST OTHER METHODS TO RETRIEVE DEP AND LIBRARIES
    public void getAllDependencies(Project project) {
        LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        for (Library library : libraryTable.getLibraries()) {
            VirtualFile[] libraryFiles = library.getFiles(OrderRootType.CLASSES);
            for (VirtualFile file : libraryFiles) {
                dependencies.add(file.getPath());
            }
            VirtualFile[] librarySourceFiles = library.getFiles(OrderRootType.SOURCES);
            for (VirtualFile file : librarySourceFiles) {
                dependencies.add(file.getPath());
            }
        }
    }

    public void getLibrariesFromFiles(List<PsiFile> psiFiles) {
        for (PsiFile psiFile : psiFiles) {
            PsiImportList importList = ((PsiJavaFile) psiFile).getImportList();
            if (importList != null) {
                PsiImportStatement[] importStatements = importList.getImportStatements();
                for (PsiImportStatement importStatement : importStatements) {
                    String libraryName = importStatement.getQualifiedName();
                    if (libraryName != null) {
                        librariesFromFiles.add(libraryName);
                    }
                }
            }
        }
    }

    public void getLibraries(Project project) {
        List<PsiFile> javaFiles = new ArrayList<>();

        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile baseDir = project.getBaseDir();

        if (baseDir != null) {
            VirtualFile srcDir = baseDir.findChild("src");
            if (srcDir != null) {
                findJavaFilesInDirectory(srcDir, javaFiles, psiManager);
            }
        }
        getLibrariesFromFiles(javaFiles);
    }

    private void findJavaFilesInDirectory(VirtualFile directory, List<PsiFile> resultList, PsiManager psiManager) {
        for (VirtualFile child : directory.getChildren()) {
            if (child.isDirectory()) {
                findJavaFilesInDirectory(child, resultList, psiManager);
            } else if (child.getName().endsWith(".java")) {
                PsiFile psiFile = psiManager.findFile(child);
                if (psiFile != null) {
                    resultList.add(psiFile);
                }
            }
        }
    }

}