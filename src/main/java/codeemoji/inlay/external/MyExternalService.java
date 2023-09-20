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

    public void initFor(@NotNull Project project) {
        // Preprocess and persist information
        persistentValues.put(project.getWorkspaceFile(), null);
    }

    @Override
    public void stopFor(@NotNull Project project) {
        // Stop processing
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void buildInfoFor(@NotNull Map infoResult, @Nullable PsiElement element) {
        try {
            if (element != null) {
                // Retrieves preprocessed values
                var value = getPersistentValue(element.getProject().getWorkspaceFile());
                // Put informations about element
                infoResult.put("externalParam", null);
            }
        } catch (RuntimeException ignored) {
        }
        System.out.println("fui chamado no projeto "
                + element.getProject().getName() + " para o elemento " + element.getText()
                + " na classe " + element.getContainingFile().getName());
    }
}