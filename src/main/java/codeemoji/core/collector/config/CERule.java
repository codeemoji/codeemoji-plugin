package codeemoji.core.collector.config;

import java.util.List;

@SuppressWarnings("unused")
public record CERule(CERuleElement element, CERuleFeature feature, String emoji, List<String> values) {
}
