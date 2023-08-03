package codeemoji.core;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class CECollector<H extends PsiElement, A extends PsiElement> extends FactoryInlayHintsCollector {

    private final String keyId;
    private final InlayPresentation inlay;

    public CECollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor);
        this.keyId = keyId;
        this.inlay = buildInlay(symbol);
    }

    public String getTooltip() {
        try {
            return CEBundle.getInstance().getBundle().getString("inlay." + getKeyId() + ".tooltip");
        } catch (RuntimeException ex) {
            return null;
        }
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

    private InlayPresentation buildInlay(@Nullable CESymbol symbol) {
        if (symbol == null) {
            symbol = new CESymbol();
        }
        PresentationFactory factory = getFactory();
        var inlay = factory.text(CEUtil.generateEmoji(symbol.getCodePoint(), symbol.getModifier(), symbol.isBackground()));
        inlay = factory.roundWithBackgroundAndSmallInset(inlay);
        String tooltip = getTooltip();
        if (tooltip != null) {
            inlay = factory.withTooltip(tooltip, inlay);
        }
        return inlay;
    }

    public abstract boolean isHintable(@NotNull H element);
}