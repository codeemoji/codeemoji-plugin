package codeemoji.inlay.external;

import lombok.Getter;

import java.util.Objects;

@Getter
public class DependencyInfo {
    // Getters
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final String path;

    public DependencyInfo(String groupId, String artifactId, String version, String path) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.path = path;
    }

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
