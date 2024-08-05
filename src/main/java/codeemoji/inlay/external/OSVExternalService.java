package codeemoji.inlay.external;

import com.intellij.openapi.components.Service;

@Service
public final class OSVExternalService extends BaseExternalServiceForVulnerability {
    private static final String OSV_API_URL = "https://api.osv.dev";
    private final OSVVulnerabilityScanner osvVulnerabilityScanner;

    public OSVExternalService() {
        this.osvVulnerabilityScanner = new OSVVulnerabilityScanner(OSV_API_URL, "", 0);
    }

    @Override
    protected VulnerabilityScanner getVulnerabilityScanner() {
        return osvVulnerabilityScanner;
    }
}