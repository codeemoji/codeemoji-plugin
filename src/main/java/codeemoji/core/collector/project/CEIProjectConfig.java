package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEConfigFile;
import codeemoji.core.collector.project.config.CERule;
import codeemoji.core.collector.project.config.CERuleElement;
import codeemoji.core.collector.project.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public interface CEIProjectConfig {

    Logger LOG = Logger.getInstance(CEIProjectConfig.class);

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

    default @NotNull CESymbol readRuleEmoji(@NotNull CERuleElement elementRule, @NotNull CERuleFeature featureRule,
                                            @Nullable CESymbol defaultSymbol) {
        for (CERule rule : getConfigFile().getRules()) {
            CERuleElement element = rule.element();
            CERuleFeature feature = rule.feature();
            if (element != null && feature != null &&
                    element.equals(elementRule) && feature.equals(featureRule)) {
                String emoji = rule.emoji();
                if (emoji != null) {
                    try {
                        int codePoint = Integer.parseInt(emoji, 16);
                        return new CESymbol(codePoint);
                    } catch (NumberFormatException ex) {
                        LOG.info(ex);
                    }
                }
            }
        }
        if (defaultSymbol == null) {
            return new CESymbol();
        }
        return defaultSymbol;
    }

    CEConfigFile getConfigFile();

    Object readConfig(String key);
}
