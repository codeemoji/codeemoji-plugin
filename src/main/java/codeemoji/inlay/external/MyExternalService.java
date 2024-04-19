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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        //getLibraries(project);
        getProjectLibraries(project);
        //getAllDependencies(project);

    }

    @Override
    public Map<VirtualFile, Object> getPersistedData() {
        return null;
    }

    public void getProjectLibraries(Project project) {
        LibraryTable librarytable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        librariesFromProject = librarytable.getLibraries();
    }


    // to ask for vulnerabilities
    private void retrieveVulnerabilitiesInfo(String libraryName, Map<String, Object> infoResult) {
        OkHttpClient client = new OkHttpClient();

        // Costruisci l'URL per la richiesta
        HttpUrl.Builder urlBuilder = HttpUrl.parse(API_BASE_URL + "vulnerabilities").newBuilder();
        urlBuilder.addQueryParameter("library", libraryName); // Aggiungi il parametro "library" con il nome della libreria
        String url = urlBuilder.build().toString();

        // Crea la richiesta HTTP
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        // Esegui la richiesta e gestisci la risposta
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                // Gestisci la risposta qui
                System.out.println("Risposta dall'API di WhiteSource: " + responseBody);
            } else {
                System.out.println("Errore durante la richiesta: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        try {
            if (element != null) {
                // Retrieves preprocessed persistent values
                var data = retrieveData(element.getProject().getWorkspaceFile());
                // Put informations about element
                infoResult.put("externalParam", null);
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