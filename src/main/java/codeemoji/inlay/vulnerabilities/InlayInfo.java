package codeemoji.inlay.vulnerabilities;

import java.util.Map;

public class InlayInfo {
    String dependencyName;
    Map<String, Integer> severityCounts;
    String scanner;

    public InlayInfo(String dependencyName, Map<String, Integer> severityCounts, String scanner) {
        this.dependencyName = dependencyName;
        this.severityCounts = severityCounts;
        this.scanner = scanner;
    }
}