package codeemoji.core.collector;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record InlayVisuals(String text, String tooltip, boolean hasBackground) {

    // direct, you must provide already translated text
    public static InlayVisuals of(@NotNull String text, @NotNull String tooltip, boolean hasBackground) {
        return new InlayVisuals(text, tooltip, hasBackground);
    }

    public static InlayVisuals of(@NotNull CESymbol symbol, @NotNull String tooltip) {
        return new InlayVisuals(symbol.getEmoji(), tooltip, symbol.isWithBackground());
    }

    public static InlayVisuals translated(@NotNull CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return translated(symbol.getEmoji(), symbol.isWithBackground(), keyTooltip, suffixTooltip);
    }

    public static @NotNull InlayVisuals translatedWithText(@NotNull String fullText, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return translated(fullText, true, keyTooltip, suffixTooltip);
    }

    // is tooltip suffix needed?
    private static @NotNull InlayVisuals translated(@NotNull String symbol, boolean background,
                                                     @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        String tooltip = CEBundle.getString(keyTooltip);
        if (null != suffixTooltip) {
            tooltip += " " + suffixTooltip;
        }
        return InlayVisuals.of(symbol, tooltip, background);
    }
}
