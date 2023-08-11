package codeemoji.core;

import com.intellij.openapi.project.Project;

public record CEProject(String name, Project project) {

    @Override
    public String toString() {
        return name;
    }
}
