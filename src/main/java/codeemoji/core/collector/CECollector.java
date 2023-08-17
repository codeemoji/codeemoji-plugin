package codeemoji.core.collector;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.DynamicInsetPresentation;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.InlayTextMetricsStorage;
import com.intellij.codeInsight.hints.presentation.InsetValueProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@Getter
public abstract class CECollector<A extends PsiElement> extends FactoryInlayHintsCollector {

    private final Editor editor;

    public CECollector(@NotNull Editor editor) {
        super(editor);
        this.editor = editor;
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (isEnabled()) {
            return processCollect(psiElement, editor, inlayHintsSink);
        }
        return false;
    }

    public void addInlayOnEditor(@Nullable A element, InlayHintsSink sink, InlayPresentation inlay) {
        if (element != null) {
            sink.addInlineElement(calcOffset(element), false, inlay, false);
        }
    }

    public int calcOffset(@Nullable A element) {
        if (element != null) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }

    public InlayPresentation buildInlay(@Nullable CESymbol symbol, @NotNull String keyTooltip) {
        return buildInlay(symbol, keyTooltip, null);
    }

    public InlayPresentation buildInlay(@Nullable CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        if (symbol == null) {
            symbol = new CESymbol();
        } else if (symbol.getIcon() != null) {
            return buildInlayWithIcon(symbol.getIcon(), keyTooltip, suffixTooltip);
        }
        return buildInlayWithSymbol(symbol.getEmoji(), keyTooltip, suffixTooltip);
    }

    private InlayPresentation buildInlayWithIcon(@NotNull Icon icon, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        var inlay = getFactory().smallScaledIcon(icon);
        return formatInlay(inlay, keyTooltip, suffixTooltip);
    }

    private InlayPresentation buildInlayWithSymbol(@NotNull String emoji, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        var inlay = getFactory().smallText(emoji);
        return formatInlay(inlay, keyTooltip, suffixTooltip);
    }

    private InlayPresentation formatInlay(@NotNull InlayPresentation inlay, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        InsetValueProvider inset = new InsetValueProvider() {
            @Override
            public int getTop() {
                return (new InlayTextMetricsStorage(getEditor())).getFontMetrics(true).offsetFromTop();
            }
        };
        inlay = new DynamicInsetPresentation(inlay, inset);
        String tooltip = getTooltip(keyTooltip);
        if (tooltip != null) {
            if (suffixTooltip != null) {
                tooltip += " " + suffixTooltip;
            }
            inlay = getFactory().withTooltip(tooltip, inlay);
        }
        return inlay;
    }

    private @Nullable String getTooltip(@NotNull String key) {
        try {
            return CEBundle.getString(key);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public boolean isEnabled() {
        return true;
    }

    public abstract boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink);

}