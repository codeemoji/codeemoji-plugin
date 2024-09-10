package codeemoji.inlay.external.services;

import codeemoji.inlay.external.scanners.OSVVulnerabilityScanner;
import codeemoji.inlay.external.scanners.VulnerabilityScanner;
import com.intellij.openapi.components.Service;

@Service
public final class OSVExternalServiceExternalService extends BaseVulnerabilityExternalService {
    private static final String OSV_API_URL = "https://api.osv.dev";
    private final OSVVulnerabilityScanner osvVulnerabilityScanner;

    public OSVExternalServiceExternalService() {
        this.osvVulnerabilityScanner = new OSVVulnerabilityScanner(OSV_API_URL, "", 0);
    }

    @Override
    protected VulnerabilityScanner getVulnerabilityScanner() {
        return osvVulnerabilityScanner;
    }
}