package codeemoji.core.collector;

import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import codeemoji.core.util.CESymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public final class CEDynamicInlayBuilder extends CEInlayBuilder {
    public CEDynamicInlayBuilder(Editor editor, SettingsKey<?> settingsKey) {
        super(editor, settingsKey);
    }

    @Override
    public InlayPresentation buildInlayWithEmoji(@NotNull CESymbol symbol, @NotNull String directTooltip, @Nullable String suffixTooltip) {
         return symbol.createPresentation(getFactory());
    }

    private @NotNull InlayPresentation formatInlay(@NotNull InlayPresentation inlay, @NotNull String directTooltip, @Nullable String suffixTooltip) {
        inlay = buildInsetValuesForInlay(inlay);
        inlay = getFactory().withCursorOnHover(inlay, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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