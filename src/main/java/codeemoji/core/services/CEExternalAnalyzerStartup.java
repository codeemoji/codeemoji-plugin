package codeemoji.core.services;

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
        var externalServices = CEExternalAnalyzer.getInstance().getExternalServices();
        for (CEExternalService infoPopulator : externalServices) {
            infoPopulator.initFor(project);
        }
        return null;
    }
}
