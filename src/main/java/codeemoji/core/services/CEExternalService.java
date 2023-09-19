package codeemoji.core.services;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface CEExternalService {

    void initFor(@NotNull Project project);
}
