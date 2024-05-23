package codeemoji.inlay.external;

import com.intellij.openapi.roots.libraries.Library;

public class DependencyParser {

    public static String parseDependencyToString(Library library) {
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

        return String.format("%s/%s@%s", groupId, artifactId, version);
    }

}
