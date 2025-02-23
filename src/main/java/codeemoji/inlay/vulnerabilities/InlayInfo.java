package codeemoji.inlay.vulnerabilities;

import java.util.Map;

public record InlayInfo(String dependencyName, Map<Severity, Integer> severityCounts, String scanner) {


    public enum Severity {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW
    }
}