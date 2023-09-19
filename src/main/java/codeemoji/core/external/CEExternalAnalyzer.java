package codeemoji.core.external;

import codeemoji.inlay.scm.MyExternalService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public final class CEExternalAnalyzer {

    private static volatile CEExternalAnalyzer instance = null;
    private final List<CEExternalService<?, ?>> externalServices = new ArrayList<>();

    private CEExternalAnalyzer(@NotNull Project project) {
        //TODO: read enableds
        externalServices.add(project.getService(MyExternalService.class));
    }

    public static CEExternalAnalyzer getInstance(@NotNull Project project) {
        if (instance == null) {
            synchronized (CEExternalAnalyzer.class) {
                if (instance == null) {
                    instance = new CEExternalAnalyzer(project);
                }
            }
        }
        return instance;
    }

    public void buildExternalInfo(@NotNull Map<?, ?> result, @Nullable PsiElement element) {
        for (CEExternalService<?, ?> service : externalServices) {
            service.buildInfo(result, element);
        }
    }
}
