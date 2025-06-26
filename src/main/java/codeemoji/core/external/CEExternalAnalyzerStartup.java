package codeemoji.core.external;

import codeemoji.inlay.vcs.RefactorService;
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
        var externalServices = CEExternalAnalyzer.getInstance().retrieveExternalServices(project);
        for (CEExternalService<?, ?> externalService : externalServices) {
            externalService.preProcess(project);
        }
        //ugly
        RefactorService.getInstance(project).preProcess();
        return null;
    }
}
