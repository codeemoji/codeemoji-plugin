package codeemoji.core.collector;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.DynamicInsetPresentation;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.InlayTextMetricsStorage;
import com.intellij.codeInsight.hints.presentation.InsetValueProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CECollector<A extends PsiElement> implements CEICollector<A> {

    private final Editor editor;

    protected CECollector(@NotNull Editor editor) {
        this.editor = editor;
    }

    private static @Nullable String getTooltip(@NotNull String key) {
        try {
            return CEBundle.getString(key);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    @Override
    public final boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (isEnabled()) {
            return processCollect(psiElement, editor, inlayHintsSink);
        }
        return false;
    }

    @Override
    public void addInlay(@Nullable A element, InlayHintsSink sink, InlayPresentation inlay) {
        if (element != null) {
            sink.addInlineElement(calcOffset(element), false, inlay, false);
        }
    }

    @Override
    public int calcOffset(@Nullable A element) {
        if (element != null) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }

    @Override
    public final InlayPresentation buildInlay(@Nullable CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
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

    private InlayPresentation buildInlayWithSymbol(@NotNull String emoji, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(getFactory().smallText(emoji), keyTooltip, suffixTooltip);
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
    @Contract("_ -> new")
    private @NotNull InlayPresentation buildInsetValuesForInlay(InlayPresentation inlay) {
        var inset = new InsetValueProvider() {
            @Override
            public int getTop() {
                return (new InlayTextMetricsStorage(getEditor())).getFontMetrics(true).offsetFromTop();
            }
        };
        return new DynamicInsetPresentation(inlay, inset);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}