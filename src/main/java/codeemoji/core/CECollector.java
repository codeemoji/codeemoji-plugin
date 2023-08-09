package codeemoji.core;

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
public abstract class CECollector<H extends PsiElement, A extends PsiElement> extends FactoryInlayHintsCollector {

    private final Editor editor;
    private final String keyId;
    private final InlayPresentation inlay;

    public CECollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor);
        this.editor = editor;
        this.keyId = keyId;
        this.inlay = buildInlay(symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (isEnabled()) {
            return processCollect(psiElement, editor, inlayHintsSink);
        }
        return false;
    }

    public void addInlayOnEditor(@Nullable A element, InlayHintsSink sink) {
        if (element != null) {
            sink.addInlineElement(calcOffset(element), false, getInlay(), false);
        }
    }

    public int calcOffset(@Nullable A element) {
        if (element != null) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }

    private InlayPresentation buildInlayWithIcon(@NotNull Icon icon) {
        var inlay = getFactory().smallScaledIcon(icon);
        return formatInlay(inlay);
    }

    private InlayPresentation buildInlayWithSymbol(@NotNull CESymbol symbol) {
        String text = CEUtil.generateEmoji(symbol.getCodePoint(), symbol.getModifier(), symbol.isBackground());
        var inlay = getFactory().smallText(text);
        return formatInlay(inlay);
    }

    private InlayPresentation buildInlay(@Nullable CESymbol symbol) {
        if (symbol == null) {
            symbol = new CESymbol();
        } else if (symbol.getIcon() != null) {
            return buildInlayWithIcon(symbol.getIcon());
        }
        return buildInlayWithSymbol(symbol);
    }

    private InlayPresentation formatInlay(@NotNull InlayPresentation inlay) {
        InsetValueProvider inset = new InsetValueProvider() {
            @Override
            public int getTop() {
                return (new InlayTextMetricsStorage(getEditor())).getFontMetrics(true).offsetFromTop();
            }
        };
        inlay = new DynamicInsetPresentation(inlay, inset);
        String tooltip = getTooltip();
        if (tooltip != null) {
            inlay = getFactory().withTooltip(tooltip, inlay);
        }
        return inlay;
    }

    private @Nullable String getTooltip() {
        try {
            return CEBundle.getInstance().getBundle().getString("inlay." + getKeyId() + ".tooltip");
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public boolean isEnabled() {
        return true;
    }

    public abstract boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink);

    public abstract boolean isHintable(@NotNull H element);
}