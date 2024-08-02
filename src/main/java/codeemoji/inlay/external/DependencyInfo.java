package codeemoji.inlay.external;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyInfo that = (DependencyInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(groupId, that.groupId) &&
                Objects.equals(artifactId, that.artifactId) &&
                Objects.equals(version, that.version) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, groupId, artifactId, version, path);
    }


}
