package codeemoji.inlay.external.services;

import codeemoji.core.config.CEGlobalSettings;
import codeemoji.inlay.external.scanners.OSSVulnerabilityScanner;
import codeemoji.inlay.external.scanners.VulnerabilityScanner;
import com.intellij.openapi.components.Service;

@Service
public final class OSSExternalServiceExternalService extends BaseVulnerabilityExternalService {
    private static final String OSS_INDEX_API_URL = "https://ossindex.sonatype.org/api/v3/component-report";
    private static final int OSS_BATCH_SIZE = 128;
    private OSSVulnerabilityScanner ossVulnerabilityScanner;

    public OSSExternalServiceExternalService() {
        updateScanner();
    }

    public void updateScanner() {
        String apiToken = CEGlobalSettings.getInstance().getOssApiToken();
        this.ossVulnerabilityScanner = new OSSVulnerabilityScanner(OSS_INDEX_API_URL, apiToken, OSS_BATCH_SIZE);
    }


    @Override
    protected VulnerabilityScanner getVulnerabilityScanner() {
        updateScanner();
        return ossVulnerabilityScanner;
    }
}