package codeemoji.inlay.external.services;

import codeemoji.inlay.external.scanners.OSSVulnerabilityScanner;
import codeemoji.inlay.external.scanners.VulnerabilityScanner;
import com.intellij.openapi.components.Service;

@Service
public final class OSSExternalServiceExternalService extends BaseVulnerabilityExternalService {
    private static final String OSS_INDEX_API_URL = "https://ossindex.sonatype.org/api/v3/component-report";
    private static final String OSS_API_TOKEN = "d8b039a32f7113c9d23e5baa798322a1e92c2202";
    private static final int OSS_BATCH_SIZE = 128;
    private final OSSVulnerabilityScanner ossVulnerabilityScanner;

    public OSSExternalServiceExternalService() {
        this.ossVulnerabilityScanner = new OSSVulnerabilityScanner(OSS_INDEX_API_URL, OSS_API_TOKEN, OSS_BATCH_SIZE);
    }

    @Override
    protected VulnerabilityScanner getVulnerabilityScanner() {
        return ossVulnerabilityScanner;
    }
}