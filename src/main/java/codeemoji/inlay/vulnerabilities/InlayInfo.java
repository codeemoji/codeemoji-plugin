package codeemoji.inlay.vulnerabilities;

import java.util.Map;

public record InlayInfo(String dependencyName, Map<String, Integer> severityCounts, String scanner) {
}