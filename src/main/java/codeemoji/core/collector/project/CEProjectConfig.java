package codeemoji.core.collector.project;

import codeemoji.core.config.CEConfigFile;
import codeemoji.core.config.CERuleElement;
import codeemoji.core.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public interface CEProjectConfig {

    default @NotNull Map<CERuleFeature, List<String>> readRuleFeatures(@NotNull CERuleElement elementRule) {
        Map<CERuleFeature, List<String>> result = new EnumMap<>(CERuleFeature.class);
        for (var rule : getConfigFile().getRules()) {
            var element = rule.element();
            var feature = rule.feature();
            if (null != feature && (element == elementRule)) {
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
            if (element == elementRule && feature == featureRule) {
                var emoji = rule.emoji();
                if (null != emoji) {
                    try {
                        var codePoint = Integer.parseInt(emoji, 16);
                        return CESymbol.of(codePoint);
                    } catch (NumberFormatException ex) {
                        Logger.getInstance(CEProjectConfig.class).info(ex);
                    }
                }
            }
        }
        if (null == defaultSymbol) {
            return CESymbol.empty();
        }
        return defaultSymbol;
    }

    CEConfigFile getConfigFile();

    @SuppressWarnings("unused")
    Object readConfig(String key);
}
