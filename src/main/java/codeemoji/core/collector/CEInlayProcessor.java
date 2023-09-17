package codeemoji.core.collector;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.presentation.DynamicInsetPresentation;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.InlayTextMetricsStorage;
import com.intellij.codeInsight.hints.presentation.InsetValueProvider;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("UnstableApiUsage")
public abstract sealed class CEInlayProcessor permits CECollector {

    private final Editor editor;
    private final @NotNull PresentationFactory factory;

    CEInlayProcessor(Editor editor) {
        this.editor = editor;
        factory = new PresentationFactory(this.editor);
    }

    private static @Nullable String getTooltip(@NotNull String key) {
        try {
            return CEBundle.getString(key);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    protected final InlayPresentation buildInlayWithEmoji(@Nullable CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        if (null == symbol) {
            symbol = new CESymbol();
        } else if (null != symbol.getIcon()) {
            return buildInlayWithIcon(symbol.getIcon(), keyTooltip, suffixTooltip);
        }
        return buildInlayWithText(symbol.getEmoji(), keyTooltip, suffixTooltip);
    }

    private InlayPresentation buildInlayWithIcon(@NotNull Icon icon, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(factory.smallScaledIcon(icon), keyTooltip, suffixTooltip);
    }

    protected final InlayPresentation buildInlayWithText(@NotNull String fullText, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(factory.smallText(fullText), keyTooltip, suffixTooltip);
    }

    private @NotNull InlayPresentation formatInlay(@NotNull InlayPresentation inlay, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        inlay = buildInsetValuesForInlay(inlay);
        inlay = factory.withCursorOnHover(inlay, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        var tooltip = getTooltip(keyTooltip);
        if (null != tooltip) {
            if (null != suffixTooltip) {
                tooltip += " " + suffixTooltip;
            }
            inlay = factory.withTooltip(tooltip, inlay);
        }
        return inlay;
    }

    //TODO: refactor internal api usage
    private @NotNull InlayPresentation buildInsetValuesForInlay(@NotNull InlayPresentation inlay) {
        var inset = new InsetValueProvider() {
            @Override
            public int getTop() {
                return (new InlayTextMetricsStorage(getEditor())).getFontMetrics(true).offsetFromTop();
            }
        };
        return new DynamicInsetPresentation(inlay, inset);
    }
}
