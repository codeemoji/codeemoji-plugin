package codeemoji.core.collector;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("UnstableApiUsage")
public interface ICECollector<A extends PsiElement> {

    private static @Nullable String getTooltip(@NotNull String key) {
        try {
            return CEBundle.getString(key);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    PresentationFactory getFactory();

    @SuppressWarnings("unused")
    Editor getEditor();

    default void addInlay(@Nullable A element, InlayHintsSink sink, InlayPresentation inlay) {
        if (element != null) {
            sink.addInlineElement(calcOffset(element), false, inlay, false);
        }
    }

    default int calcOffset(@Nullable A element) {
        if (element != null) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }

    default InlayPresentation buildInlay(@Nullable CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        if (symbol == null) {
            symbol = new CESymbol();
        } else if (symbol.getIcon() != null) {
            return buildInlayWithIcon(symbol.getIcon(), keyTooltip, suffixTooltip);
        }
        return buildInlayWithSymbol(symbol.getEmoji(), keyTooltip, suffixTooltip);
    }

    default InlayPresentation buildInlayWithIcon(@NotNull Icon icon, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        var inlay = getFactory().smallScaledIcon(icon);
        return formatInlay(inlay, keyTooltip, suffixTooltip);
    }

    default InlayPresentation buildInlayWithSymbol(@NotNull String emoji, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        var inlay = getFactory().smallText(emoji);
        return formatInlay(inlay, keyTooltip, suffixTooltip);
    }

    default InlayPresentation formatInlay(@NotNull InlayPresentation inlay, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
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

    default boolean isEnabled() {
        return true;
    }

    @SuppressWarnings("SameReturnValue")
    boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink);

}