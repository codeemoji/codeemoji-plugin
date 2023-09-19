package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public final class MyExternalService implements CEExternalService<VirtualFile, Object> {

    Map<VirtualFile, Object> persistentValues = new HashMap<>();

    public void init(@NotNull Project project) {
        // Persists preprocessing values
        persistentValues.put(project.getWorkspaceFile(), null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        try {
            if (element != null) {
                // Retrieves preprocessed values
                var value = getPersistentValue(element.getProject().getWorkspaceFile());
                // Put informations about element
                infoResult.put("externalParam", null);
            }
        } catch (RuntimeException ignored) {
        }
    }
}