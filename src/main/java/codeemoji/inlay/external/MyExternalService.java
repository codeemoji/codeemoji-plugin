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

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public final class MyExternalService implements CEExternalService<VirtualFile, Object> {

    Map<VirtualFile, Object> persistedData = new HashMap<>(); // is this really necessary?
    HashMap<Library, JSONArray> hashMap = new HashMap<>();

    Library[] librariesFromProject = null;

    DependencyParser parser = new DependencyParser();
    DependencyChecker checker = new DependencyChecker();

    /*public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        getProjectLibraries(project);

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
    }*/

    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        getProjectLibraries(project);

        for (Library lib : librariesFromProject) {
            try {
                String library = parser.parseDependencyToString(lib);
                JSONObject vulnerability = checker.checkDependency(library);

                String coordinates = vulnerability.getString("coordinates");
                JSONArray vulnerabilities = vulnerability.getJSONArray("vulnerabilities");

                if (vulnerabilities.length() != 0) {
                    hashMap.putIfAbsent(lib, vulnerabilities);
                } else {
                    System.out.println(coordinates + " not added to hashmap because no vulnerabilities have been found");
                }
            } catch (JSONException e) {
                System.err.println("Error processing library: " + lib.getName() + ". Error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error processing library: " + lib.getName() + ". Error: " + e.getMessage());
            }
        }

        System.out.println(hashMap);
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