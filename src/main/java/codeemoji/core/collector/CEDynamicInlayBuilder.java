package codeemoji.core.collector;

import com.intellij.openapi.editor.Editor;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import codeemoji.core.util.CESymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CEDynamicInlayBuilder extends CEInlayBuilder {
    public CEDynamicInlayBuilder(Editor editor) {
        super(editor);
    }

    @Override
    public InlayPresentation buildInlayWithEmoji(@Nullable CESymbol symbol, @NotNull String directTooltip, @Nullable String suffixTooltip) {
        if (null == symbol) {
            symbol = new CESymbol();
        } else if (null != symbol.getIcon()) {
            return buildInlayWithIcon(symbol.getIcon(), directTooltip, suffixTooltip);
        }
        return buildInlayWithText(symbol.getEmoji(), directTooltip, suffixTooltip);
    }

    private @NotNull InlayPresentation buildInlayWithIcon(@NotNull javax.swing.Icon icon, @NotNull String directTooltip, @Nullable String suffixTooltip) {
        return formatInlay(getFactory().smallScaledIcon(icon), directTooltip, suffixTooltip);
    }

    @Override
    protected @NotNull InlayPresentation buildInlayWithText(@NotNull String fullText, @NotNull String directTooltip, @Nullable String suffixTooltip) {
        return formatInlay(getFactory().smallText(fullText), directTooltip, suffixTooltip);
    }

    private @NotNull InlayPresentation formatInlay(@NotNull InlayPresentation inlay, @NotNull String directTooltip, @Nullable String suffixTooltip) {
        inlay = buildInsetValuesForInlay(inlay);
        inlay = getFactory().withCursorOnHover(inlay, java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        String tooltip = directTooltip;
        if (null != suffixTooltip) {
            tooltip += " " + suffixTooltip;
        }
        inlay = getFactory().withTooltip(tooltip, inlay);

        return inlay;
    }

    private @NotNull InlayPresentation buildInsetValuesForInlay(@NotNull InlayPresentation inlay) {
        return getFactory().roundWithBackground(inlay);
    }
}