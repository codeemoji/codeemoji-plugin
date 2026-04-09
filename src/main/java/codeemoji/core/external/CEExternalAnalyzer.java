package codeemoji.core.external;

import codeemoji.core.config.CEGlobalSettings;
import codeemoji.inlay.external.*;
import codeemoji.inlay.external.services.OSSExternalServiceExternalService;
import codeemoji.inlay.external.services.OSVExternalServiceExternalService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class CEExternalAnalyzer {

    public static CEExternalAnalyzer getInstance() {
        return CEExternalAnalyzerHolder.INSTANCE;
    }

    public @NotNull List<CEExternalService<?, ?>> retrieveExternalServices(@NotNull Project project) {
        List<CEExternalService<?, ?>> externalServices = new ArrayList<>();
        CEGlobalSettings globalSettings = CEGlobalSettings.getInstance();
        boolean myExternalServiceState = globalSettings.getMyExternalServiceState();
        if (myExternalServiceState) {
            VulnerabilityInfo.ScannerType scannerType = globalSettings.getScannerType();
            if (scannerType.equals(VulnerabilityInfo.ScannerType.OSS)) {
                OSSExternalServiceExternalService ossService = project.getService(OSSExternalServiceExternalService.class);
                ossService.updateScanner();
                externalServices.add(ossService);
            } else if (scannerType.equals(VulnerabilityInfo.ScannerType.OSV)) {
                externalServices.add(project.getService(OSVExternalServiceExternalService.class));
            }
        }
        return externalServices;
    }

    public void buildExternalInfo(@NotNull Map<?, ?> result, @NotNull PsiElement element) {
        for (CEExternalService<?, ?> service : retrieveExternalServices(element.getProject())) {
            service.buildInfo(result, element);
        }
    }

    private static final class CEExternalAnalyzerHolder {
        private static final CEExternalAnalyzer INSTANCE = new CEExternalAnalyzer();
    }
}
