package codeemoji.core.collector;

import codeemoji.core.util.CESymbol;

public record InlayVisuals(String text, String tooltip, boolean hasBackground) {

    public static InlayVisuals of(String text, String tooltip, boolean hasBackground) {
        return new InlayVisuals(text, tooltip, hasBackground);
    }

    public static InlayVisuals of(CESymbol symbol, String tooltip) {
        return new InlayVisuals(symbol.getEmoji(), tooltip, symbol.isWithBackground());
    }
}
