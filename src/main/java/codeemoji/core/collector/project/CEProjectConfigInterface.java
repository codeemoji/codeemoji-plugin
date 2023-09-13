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

    default @NotNull Map<CERuleFeature, List<String>> readRuleFeatures(@NotNull final CERuleElement elementRule) {
        final Map<CERuleFeature, List<String>> result = new EnumMap<>(CERuleFeature.class);
        for (final var rule : this.getConfigFile().getRules()) {
            final var element = rule.element();
            final var feature = rule.feature();
            if (null != element && null != feature && (element == elementRule)) {
                result.put(feature, rule.values());
            }
        }
        return result;
    }

    default @NotNull CESymbol readRuleEmoji(@NotNull final CERuleElement elementRule, @NotNull final CERuleFeature featureRule,
                                            @Nullable final CESymbol defaultSymbol) {
        for (final var rule : this.getConfigFile().getRules()) {
            final var element = rule.element();
            final var feature = rule.feature();
            if (null != element && null != feature &&
                    element == elementRule && feature == featureRule) {
                final var emoji = rule.emoji();
                if (null != emoji) {
                    try {
                        final var codePoint = Integer.parseInt(emoji, 16);
                        return new CESymbol(codePoint);
                    } catch (final NumberFormatException ex) {
                        CEProjectConfigInterface.LOG.info(ex);
                    }
                }
            }
        }
        if (null == defaultSymbol) {
            return new CESymbol();
        }
        return defaultSymbol;
    }

    CEConfigFile getConfigFile();

    @SuppressWarnings("unused")
    Object readConfig(String key);
}
