package codeemoji.core;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static codeemoji.core.CESymbol.DEFAULT;

@Getter
public abstract class CECollector<T extends PsiElement, V extends PsiElement> extends FactoryInlayHintsCollector {

    private final String keyId;

    public CECollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor);
        this.keyId = keyId;
    }

    @Override
    public final boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        try {
            return CEUtil.isPreviewEditor(editor) ?
                    collectForPreviewEditor(psiElement, inlayHintsSink) : collectForRegularEditor(psiElement, inlayHintsSink);
        } catch (RuntimeException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
        return false;
    }

    public abstract boolean collectForPreviewEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink);

    public abstract boolean collectForRegularEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink);

    public String getTooltipText() {
        try {
            return CEBundle.getInstance().getMessages().getString("inlay." + getKeyId() + ".tooltip");
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public void addInlayHint(@NotNull V element, @NotNull InlayHintsSink sink) {
        addInlayHint(element, sink, DEFAULT);
    }

    public void addInlayHint(@NotNull V element, @NotNull InlayHintsSink sink, @NotNull CESymbol codePoint) {
        addInlayHint(element, sink, codePoint.getValue(), 0);
    }

    public void addInlayHint(@NotNull V element, @NotNull InlayHintsSink sink, int codePoint) {
        addInlayHint(element, sink, codePoint, 0);
    }

    public void addInlayHint(@NotNull V element, @NotNull InlayHintsSink sink, @NotNull CESymbol codePoint, int modifier) {
        addInlayHint(element, sink, codePoint.getValue(), modifier, true);
    }

    public void addInlayHint(@NotNull V element, @NotNull InlayHintsSink sink, int codePoint, int modifier) {
        addInlayHint(element, sink, codePoint, modifier, true);
    }

    public void addInlayHint(@NotNull V element, @NotNull InlayHintsSink sink, @NotNull CESymbol codePoint, int modifier, boolean addColor) {
        addInlayHint(element, sink, codePoint.getValue(), modifier, true);
    }

    public void addInlayHint(@NotNull V element, @NotNull InlayHintsSink sink, int codePoint, int modifier, boolean addColor) {
        InlayPresentation inlay = configureInlayHint(codePoint, modifier, addColor);
        sink.addInlineElement(element.getTextOffset() + element.getTextLength(), false, inlay, false);
    }

    private @NotNull InlayPresentation configureInlayHint(int codePoint, int modifier, boolean addColor) {
        PresentationFactory factory = getFactory();
        var inlay = factory.text(CEUtil.generateEmoji(codePoint, modifier, addColor));
        inlay = factory.roundWithBackgroundAndSmallInset(inlay);
        String tooltipText = getTooltipText();
        if (tooltipText != null) {
            inlay = factory.withTooltip(tooltipText, inlay);
        }
        return inlay;
    }

    public abstract void execute(T element, InlayHintsSink sink);
}