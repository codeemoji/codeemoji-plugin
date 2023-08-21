package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEConfigFile;
import codeemoji.core.collector.project.config.CERule;
import codeemoji.core.collector.project.config.CERuleElement;
import codeemoji.core.collector.project.config.CERuleFeature;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public interface CEIProjectConfig {

    default Map<CERuleFeature, List<String>> readRuleFeatures(@NotNull CERuleElement elementRule) {
        Map<CERuleFeature, List<String>> result = new EnumMap<>(CERuleFeature.class);
        for (CERule rule : getConfigFile().getRules()) {
            CERuleElement element = rule.element();
            CERuleFeature feature = rule.feature();
            if (element != null && feature != null && (element.equals(elementRule))) {
                result.put(feature, rule.values());
            }
        }
        return result;
    }

    CEConfigFile getConfigFile();

    Object getConfig(String key);
}
