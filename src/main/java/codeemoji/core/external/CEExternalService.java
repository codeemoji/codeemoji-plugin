package codeemoji.core.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CEExternalService<K, V> {

    void init(@NotNull Project project);

    void buildInfo(@NotNull Map<?, ?> infoResult, @Nullable PsiElement element);

    default V getPersistentValue(@Nullable K projectKey) {
        try {
            return getPersistentValues().get(projectKey);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    Map<K, V> getPersistentValues();
}
