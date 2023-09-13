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
public abstract sealed class CEInlayProcessor permits CECollector {

    private final Editor editor;
    private final @NotNull PresentationFactory factory;

    protected CEInlayProcessor(final Editor editor) {
        this.editor = editor;
        factory = new PresentationFactory(this.editor);
    }

    private static @Nullable String getTooltip(@NotNull final String key) {
        try {
            return CEBundle.getString(key);
        } catch (final RuntimeException ex) {
            return null;
        }
    }

    protected final InlayPresentation buildInlayWithEmoji(@Nullable CESymbol symbol, @NotNull final String keyTooltip, @Nullable final String suffixTooltip) {
        if (null == symbol) {
            symbol = new CESymbol();
        } else if (null != symbol.getIcon()) {
            return this.buildInlayWithIcon(symbol.getIcon(), keyTooltip, suffixTooltip);
        }
        return this.buildInlayWithText(symbol.getEmoji(), keyTooltip, suffixTooltip);
    }

    protected InlayPresentation buildInlayWithIcon(@NotNull final Icon icon, @NotNull final String keyTooltip, @Nullable final String suffixTooltip) {
        return this.formatInlay(factory.smallScaledIcon(icon), keyTooltip, suffixTooltip);
    }

    protected InlayPresentation buildInlayWithText(@NotNull final String fullText, @NotNull final String keyTooltip, @Nullable final String suffixTooltip) {
        return this.formatInlay(factory.smallText(fullText), keyTooltip, suffixTooltip);
    }

    private @NotNull InlayPresentation formatInlay(@NotNull InlayPresentation inlay, @NotNull final String keyTooltip, @Nullable final String suffixTooltip) {
        inlay = this.buildInsetValuesForInlay(inlay);
        inlay = factory.withCursorOnHover(inlay, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        var tooltip = CEInlayProcessor.getTooltip(keyTooltip);
        if (null != tooltip) {
            if (null != suffixTooltip) {
                tooltip += " " + suffixTooltip;
            }
            inlay = factory.withTooltip(tooltip, inlay);
        }
        return inlay;
    }

    //TODO: refactor internal api usage
    private @NotNull InlayPresentation buildInsetValuesForInlay(@NotNull final InlayPresentation inlay) {
        final var inset = new InsetValueProvider() {
            @Override
            public int getTop() {
                return (new InlayTextMetricsStorage(CEInlayProcessor.this.getEditor())).getFontMetrics(true).offsetFromTop();
            }
        };
        return new DynamicInsetPresentation(inlay, inset);
    }
}
