package codeemoji.core.collector.project;

import codeemoji.core.collector.config.CEConfigFile;
import codeemoji.core.collector.config.CERuleElement;
import codeemoji.core.collector.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public interface CEProjectConfigInterface {

    Logger LOG = Logger.getInstance(CEProjectConfigInterface.class);

    default @NotNull Map<CERuleFeature, List<String>> readRuleFeatures(@NotNull CERuleElement elementRule) {
        Map<CERuleFeature, List<String>> result = new EnumMap<>(CERuleFeature.class);
        for (var rule : getConfigFile().getRules()) {
            var element = rule.element();
            var feature = rule.feature();
            if (element != null && feature != null && (element.equals(elementRule))) {
                result.put(feature, rule.values());
            }
        }
        return result;
    }

    default @NotNull CESymbol readRuleEmoji(@NotNull CERuleElement elementRule, @NotNull CERuleFeature featureRule,
                                            @Nullable CESymbol defaultSymbol) {
        for (var rule : getConfigFile().getRules()) {
            var element = rule.element();
            var feature = rule.feature();
            if (element != null && feature != null &&
                    element.equals(elementRule) && feature.equals(featureRule)) {
                var emoji = rule.emoji();
                if (emoji != null) {
                    try {
                        var codePoint = Integer.parseInt(emoji, 16);
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

    @SuppressWarnings("unused")
    Object readConfig(String key);
}
