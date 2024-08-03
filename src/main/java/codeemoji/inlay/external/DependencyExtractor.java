package codeemoji.inlay.external;

import com.intellij.openapi.roots.libraries.Library;

// This class returns a List of object containing information about the dependencies of the given project
public class DependencyExtractor {
    public static DependencyInfo getDependecyInfo(Library library) {
        String dependencyName = library.getName();
        if (!dependencyName.startsWith("Gradle: ")) {
            throw new IllegalArgumentException("Invalid format: not starting with Gradle");
        }
        // Removing "Gradle: "
        String dependency = dependencyName.substring(8);

        String[] parts = dependency.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid format: no groupId, artifactId or version");
        }
        String groupId = parts[0];
        String artifactId = parts[1];
        String version = parts[2];

        // This is the path, which is the return value of the original function
        String path = String.format("%s/%s@%s", groupId, artifactId, version);

        // Process groupId and artifactId as in the first method
        if (groupId.contains(".")) {
            String[] groupParts = groupId.split("\\.");
            groupId = groupParts[1];
        }
        if (artifactId.contains("-")) {
            String[] artifactParts = artifactId.split("-");
            artifactId = artifactParts[0];
        }

        return new DependencyInfo(groupId, artifactId, version, path);
    }
}