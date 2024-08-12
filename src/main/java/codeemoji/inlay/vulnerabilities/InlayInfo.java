package codeemoji.inlay.vulnerabilities;

public class InlayInfo {
    String dependencyName;
    int numberOfVulnerabilities;
    String scanner;

    public InlayInfo(String dependencyName, int numberOfVulnerabilities, String scanner) {
        this.dependencyName = dependencyName;
        this.numberOfVulnerabilities = numberOfVulnerabilities;
        this.scanner = scanner;
    }
}
