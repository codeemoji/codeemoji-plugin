package codeemoji.core.services;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

@Service
public final class GitExternalService implements CEExternalService {

    @Override
    public void initFor(@NotNull Project project) {
        System.out.println("Oi, sou um serviço que foi iniciado para o projet " + project.getName());
    }
}
