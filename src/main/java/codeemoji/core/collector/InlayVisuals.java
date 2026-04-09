package codeemoji.core.collector;

import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import org.jetbrains.annotations.NotNull;

public record InlayVisuals(String text, String tooltip, boolean hasBackground) {

    public static InlayVisuals direct(@NotNull CESymbol symbol, @NotNull String keyTooltip) {
        return new InlayVisuals(symbol.getEmoji(),keyTooltip, symbol.isWithBackground());
    }

    public static InlayVisuals translated(@NotNull CESymbol symbol, @NotNull String keyTooltip, @NotNull Object ...args) {
        return translated(symbol.getEmoji(), symbol.isWithBackground(), keyTooltip, args);
    }

    public static @NotNull InlayVisuals translatedWithText(@NotNull String fullText, @NotNull String keyTooltip, @NotNull Object ...args) {
        return translated(fullText, true, keyTooltip, args);
    }

    // is tooltip suffix needed?
    private static @NotNull InlayVisuals translated(@NotNull String symbol, boolean background,
                                                     @NotNull String keyTooltip, @NotNull Object ...args) {
        String tooltip = CEBundle.getString(keyTooltip, args);
        return new InlayVisuals(symbol, tooltip, background);
    }

    //maybe ugly
    public static InlayVisuals fromProviderSimple(CEProvider<?> ceProvider){
        var tooltipKey = "inlay." + ceProvider.getKey() + ".tooltip";
        var symbolGetter = ceProvider.getSettings().getMainSymbol();
        return InlayVisuals.translated(symbolGetter, tooltipKey);
    }
}
