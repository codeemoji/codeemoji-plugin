package codeemoji.core;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class CECollector<P extends PsiElement, A extends PsiElement> extends FactoryInlayHintsCollector {

    private final String keyId;
    private final InlayPresentation inlay;

    public CECollector(Editor editor, String keyId) {
        super(editor);
        this.keyId = keyId;
        inlay = buildInlay(new CEInlay());
    }

    public CECollector(Editor editor, String keyId, CEInlay ceInlay) {
        super(editor);
        this.keyId = keyId;
        this.inlay = buildInlay(ceInlay);
    }

    public CECollector(Editor editor, String keyId, int codePoint) {
        super(editor);
        this.keyId = keyId;
        this.inlay = buildInlay(new CEInlay(codePoint));
    }

    public CECollector(Editor editor, String keyId, CESymbol symbol) {
        super(editor);
        this.keyId = keyId;
        this.inlay = buildInlay(new CEInlay(symbol));
    }

    public String getTooltip() {
        try {
            return CEBundle.getInstance().getBundle().getString("inlay." + getKeyId() + ".tooltip");
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public void addInlayOnEditor(A element, InlayHintsSink sink) {
        if (element != null) {
            sink.addInlineElement(element.getTextOffset() + element.getTextLength(), false, getInlay(), false);
        }
    }

    private InlayPresentation buildInlay(@NotNull CEInlay ceInlay) {
        PresentationFactory factory = getFactory();
        var inlay = factory.text(CEUtil.generateEmoji(ceInlay.getCodePoint(), ceInlay.getModifier(), ceInlay.isBackground()));
        inlay = factory.roundWithBackgroundAndSmallInset(inlay);
        String tooltip = getTooltip();
        if (tooltip != null) {
            inlay = factory.withTooltip(tooltip, inlay);
        }
        return inlay;
    }

    public abstract boolean putHintHere(@NotNull P element);
}