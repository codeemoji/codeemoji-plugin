package codeemoji.core.collector.project.config;

import java.util.List;

public record CERule(CERuleElement element, CERuleFeature feature, String emoji, List<String> values) {
}
