package codeemoji.core.collector;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.presentation.*;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract sealed class CEInlayProcessor permits CECollectorInline, CECollectorBlock {

    private final Editor editor;
    private final PresentationFactory factory;

    public CEInlayProcessor(Editor editor) {
        this.editor = editor;
        this.factory = new PresentationFactory(getEditor());
    }

    private static @Nullable String getTooltip(@NotNull String key) {
        try {
            return CEBundle.getString(key);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    protected final InlayPresentation buildInlay(@Nullable CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        if (symbol == null) {
            symbol = new CESymbol();
        } else if (symbol.getIcon() != null) {
            return buildInlayWithIcon(symbol.getIcon(), keyTooltip, suffixTooltip);
        }
        return buildInlayWithSymbol(symbol.getEmoji(), keyTooltip, suffixTooltip);
    }

    private InlayPresentation buildInlayWithIcon(@NotNull Icon icon, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(getFactory().smallScaledIcon(icon), keyTooltip, suffixTooltip);
    }

    private InlayPresentation buildInlayWithSymbol(@NotNull String fullText, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(getFactory().smallText(fullText), keyTooltip, suffixTooltip);
    }

    private InlayPresentation formatInlay(@NotNull InlayPresentation inlay, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        inlay = buildInsetValuesForInlay(inlay);
        inlay = getFactory().withCursorOnHover(inlay, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        var tooltip = getTooltip(keyTooltip);
        if (tooltip != null) {
            if (suffixTooltip != null) {
                tooltip += " " + suffixTooltip;
            }
            inlay = getFactory().withTooltip(tooltip, inlay);
        }
        return inlay;
    }

    //TODO: refactor internal api usage
    private @NotNull InlayPresentation buildInsetValuesForInlay(InlayPresentation inlay) {
        var inset = new InsetValueProvider() {
            @Override
            public int getTop() {
                return (new InlayTextMetricsStorage(getEditor())).getFontMetrics(true).offsetFromTop();
            }
        };
        return new DynamicInsetPresentation(inlay, inset);
    }
}
