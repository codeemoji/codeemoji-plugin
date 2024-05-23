package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
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
    Library[] librariesFromProject = null;

    DependencyParser parser = new DependencyParser();
    DependencyChecker checker = new DependencyChecker();



    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        getProjectLibraries(project);
        getVulnerabilities();


        System.out.println(hashMap);
    }

    public void getVulnerabilities() {
        List<String> libraryCoordinates = new ArrayList<>();

        for (Library lib : librariesFromProject) {
            try {
                String library = parser.parseDependencyToString(lib);
                libraryCoordinates.add("pkg:maven/" + library);
            } catch (IllegalArgumentException e) {
                System.err.println("Error processing library: " + lib.getName() + ". Error: " + e.getMessage());
            }
        }

        JSONArray vulnerabilityData = checker.checkDependencies(libraryCoordinates);

        for (String lib : libraryCoordinates) {
            for (int i = 0; i < vulnerabilityData.length(); i++) {
                try {
                    // Ottieni l'oggetto JSON corrente dall'array
                    JSONObject jsonObject = vulnerabilityData.getJSONObject(i);

                    // Estrai le coordinate dall'oggetto JSON corrente
                    String coordinates = jsonObject.getString("coordinates");

                    // Controlla se le coordinate correnti corrispondono a quelle desiderate
                    if (coordinates.equals(lib)) {
                        // Controlla se il campo "vulnerabilities" non è vuoto
                        JSONArray vulnerabilities = jsonObject.getJSONArray("vulnerabilities");
                        if (vulnerabilities.length() > 0) {
                            // Aggiungi l'intero oggetto JSON corrispondente all'HashMap
                            hashMap.put(librariesFromProject[i], vulnerabilities);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private boolean checkIfLibrariesChanged(@Nullable PsiElement element) {
        LibraryTable librarytable = LibraryTablesRegistrar.getInstance().getLibraryTable(element.getProject());
        Library[] newLibrariesFromProject = librarytable.getLibraries();
        if (newLibrariesFromProject.length != librariesFromProject.length) {
            return true;
        }
        for (int i = 0; i < newLibrariesFromProject.length; i++) {
            if (newLibrariesFromProject[i].getName() != librariesFromProject[i].getName()) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        if (checkIfLibrariesChanged(element)) {
            System.out.println("Libraries of project changed");
            getProjectLibraries(element.getProject());
            getVulnerabilities();
        }
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