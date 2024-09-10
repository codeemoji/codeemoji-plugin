package codeemoji.inlay.external;

import java.util.Objects;

public class DependencyInfo {
    private String groupId;
    private String artifactId;
    private String version;
    private String path;

    public DependencyInfo(String groupId, String artifactId, String version, String path) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.path = path;
    }

    // Getters
    public String getGroupId() { return groupId; }
    public String getArtifactId() { return artifactId; }
    public String getVersion() { return version; }
    public String getPath() { return path; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyInfo that = (DependencyInfo) o;
        return Objects.equals(groupId, that.groupId) &&
                    Objects.equals(artifactId, that.artifactId) &&
                    Objects.equals(version, that.version) &&
                    Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version, path);
    }


}
