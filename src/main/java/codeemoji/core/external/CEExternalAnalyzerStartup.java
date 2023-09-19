package codeemoji.core.external;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CEExternalAnalyzerStartup implements ProjectActivity {

    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        var externalServices = CEExternalAnalyzer.getInstance(project).getExternalServices();
        for (CEExternalService<?, ?> externalService : externalServices) {
            externalService.init(project);
        }
        return null;
    }
}
