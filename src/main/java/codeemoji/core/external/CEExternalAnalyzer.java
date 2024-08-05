package codeemoji.core.external;

import codeemoji.core.config.CEGlobalSettings;
import codeemoji.inlay.external.*;
import codeemoji.inlay.external.services.OSSExternalServiceExternalService;
import codeemoji.inlay.external.services.OSVExternalServiceExternalService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CEExternalAnalyzer {

    public static CEExternalAnalyzer getInstance() {
        return CEExternalAnalyzerHolder.INSTANCE;
    }

    public @NotNull List<CEExternalService<?, ?>> retrieveExternalServices(@NotNull Project project) {
        List<CEExternalService<?, ?>> externalServices = new ArrayList<>();
        var globalSettings = CEGlobalSettings.getInstance();
        var myExternalServiceState = globalSettings.getMyExternalServiceState();
        if (myExternalServiceState) {
            VulnerabilityInfo.ScannerType scannerType = globalSettings.getType();
            if (scannerType.equals(VulnerabilityInfo.ScannerType.OSV)) {
                externalServices.add(project.getService(OSVExternalServiceExternalService.class));
            } else if (scannerType.equals(VulnerabilityInfo.ScannerType.OSS)) {
                externalServices.add(project.getService(OSSExternalServiceExternalService.class));
            }
        }
        return externalServices;
    }

    public void buildExternalInfo(@NotNull Map<?, ?> result, @Nullable PsiElement element) {
        if (element != null) {
            for (CEExternalService<?, ?> service : retrieveExternalServices(element.getProject())) {
                service.buildInfo(result, element);
            }
        }
    }

    private static final class CEExternalAnalyzerHolder {
        private static final CEExternalAnalyzer INSTANCE = new CEExternalAnalyzer();
    }
}
