package codeemoji.inlay.external;

public class DependencyInfo {
    private String name;
    private String groupId;
    private String artifactId;
    private String version;
    private String path;

    public DependencyInfo(String name, String groupId, String artifactId, String version, String path) {
        this.name = name;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.path = path;
    }

    // Getters
    public String getName() { return name; }
    public String getGroupId() { return groupId; }
    public String getArtifactId() { return artifactId; }
    public String getVersion() { return version; }
    public String getPath() { return path; }

}
