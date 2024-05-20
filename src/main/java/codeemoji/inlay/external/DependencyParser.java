package codeemoji.inlay.external;

import com.intellij.openapi.roots.libraries.Library;

public class DependencyParser {

    public static String parseDependencyToString(Library library) {
        String dependencyName = library.getName();
        if (!dependencyName.startsWith("Gradle: ")) {
            throw new IllegalArgumentException("Formato di dipendenza non valido");
        }

        // Rimuovi il prefisso "Gradle: "
        String dependency = dependencyName.substring(8);

        // Dividi la stringa rimanente usando ":" come delimitatore
        String[] parts = dependency.split(":");

        // Verifica che ci siano esattamente 3 parti (groupId, artifactId, version)
        if (parts.length != 3) {
            throw new IllegalArgumentException("Formato di dipendenza non valido");
        }

        // Costruisci la stringa nel formato desiderato
        String groupId = parts[0];
        String artifactId = parts[1];
        String version = parts[2];

        return String.format("%s/%s@%s", groupId, artifactId, version);
    }

    private static String getVersion() {
        return "";
    }

    private static String getArtifactId() {
        return "";
    }

    private static String getGroupId() {
        return "";
    }
}
