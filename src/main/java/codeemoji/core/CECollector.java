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
public abstract class CECollector extends FactoryInlayHintsCollector {

    private final String keyId;

    public CECollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor);
        this.keyId = keyId;
    }

    public abstract boolean collectForPreviewEditor(PsiElement element, InlayHintsSink sink);

    public String getTooltipText() {
        try {
            return CEBundle.getInstance().getMessages().getString("inlay." + getKeyId() + ".tooltip");
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public void addInlayHint(@NotNull PsiElement element, @NotNull InlayHintsSink sink, int codePoint) {
        addInlayHint(element, sink, codePoint, false);
    }

    public void addInlayHint(@NotNull PsiElement element, @NotNull InlayHintsSink sink, int codePoint, boolean addColor) {
        InlayPresentation inlay = configureInlayHint(codePoint, addColor);
        sink.addInlineElement(element.getTextOffset() + element.getTextLength(), false, inlay, false);
    }

    public @NotNull InlayPresentation configureInlayHint(int codePoint, boolean addColor) {
        PresentationFactory factory = getFactory();
        var inlay = factory.text(CEUtil.generateEmoji(codePoint, addColor));
        inlay = factory.roundWithBackgroundAndSmallInset(inlay);
        String tooltipText = getTooltipText();
        if (tooltipText != null) {
            inlay = factory.withTooltip(tooltipText, inlay);
        }
        return inlay;
    }

    public abstract void processInlayHint(PsiElement element, InlayHintsSink sink);
}