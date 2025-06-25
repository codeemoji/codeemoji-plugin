package codeemoji.inlay.external;

import lombok.Getter;

/**
 * @param groupId Getters
 */
@Getter
public record DependencyInfo(String groupId, String artifactId, String version, String path) {


}
