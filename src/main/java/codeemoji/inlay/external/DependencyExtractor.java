package codeemoji.inlay.external;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// This class returns a List of object containing information about the dependencies of the given project
public class DependencyExtractor {

    public List<JSONObject> extractProjectDependencies(Project project) {
        List<JSONObject> dependencies = new ArrayList<>();
        ModuleManager moduleManager = ModuleManager.getInstance(project);

        for (Module module : moduleManager.getModules()) {
            ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
            for (OrderEntry entry : rootManager.getOrderEntries()) {
                if (entry instanceof LibraryOrderEntry) {
                    LibraryOrderEntry libraryEntry = (LibraryOrderEntry) entry;
                    JSONObject dependencyObject = parseDependency(libraryEntry);
                    if (dependencyObject != null) {
                        addIfNotExists(dependencies, dependencyObject);
                    }
                }
            }
        }
        return dependencies;
    }

    private static void addIfNotExists(List<JSONObject> jsonList, JSONObject jsonObject) {
        for (JSONObject existingObject : jsonList) {
            if (existingObject.similar(jsonObject)) {
                return;
            }
        }
        jsonList.add(jsonObject);
    }

    private JSONObject parseDependency(LibraryOrderEntry libraryEntry) {
        String libraryName = libraryEntry.getLibraryName();
        if (libraryName == null) return null;

        if (libraryName.startsWith("Gradle: ")) {
            return parseGradleDependency(libraryName);
        }
        else {
            // Add more parsing logic for other build systems if needed
        }
        return null;
    }

    private JSONObject parseGradleDependency(String libraryName) {
        JSONObject dependencyObject = new JSONObject();
        String dependency = libraryName.substring(8); // Remove "Gradle: "
        String[] parts = dependency.split(":");
        if (parts.length != 3) {
            return null;
        }
        String groupId = parts[0];
        String artifactId = parts[1];
        String version = parts[2];
        String name = String.format("%s/%s@%s", groupId, artifactId, version);
        if (groupId.contains(".")) {
            String[] groupParts = groupId.split("\\.");
            groupId = groupParts[1];
        }
        if (artifactId.contains("-")) {
            String[] artifactParts = artifactId.split("-");
            artifactId = artifactParts[0];
        }
        dependencyObject.put("name", name);
        dependencyObject.put("groupId", groupId);
        dependencyObject.put("artifactId", artifactId);
        dependencyObject.put("version", version);

        return dependencyObject;
    }
}