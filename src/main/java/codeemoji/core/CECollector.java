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

import java.util.Arrays;

import static codeemoji.core.CESymbol.DEFAULT;

@Getter
public abstract class CECollector<T extends PsiElement, V extends PsiElement> extends FactoryInlayHintsCollector {

    private final String keyId;

    public CECollector(@NotNull Editor editor) {
        super(editor);
        this.keyId = getClass().getSimpleName().toLowerCase();
    }

    @Override
    public final boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        try {
            return CEUtil.isPreviewEditor(editor) ?
                    collectInPreviewEditor(psiElement, inlayHintsSink) : collectInDefaultEditor(psiElement, inlayHintsSink);
        } catch (RuntimeException ex) {
            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
        return false;
    }

    public abstract boolean collectInPreviewEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) throws RuntimeException;

    public abstract boolean collectInDefaultEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) throws RuntimeException;

    public @Nullable String getTooltip() {
        try {
            return CEBundle.getInstance().getMessages().getString("inlay." + getKeyId() + ".tooltip");
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public final void addInlay(@NotNull V element, @NotNull InlayHintsSink sink) {
        addInlay(element, sink, DEFAULT);
    }

    public final void addInlay(@NotNull V element, @NotNull InlayHintsSink sink, @NotNull CESymbol codePoint) {
        addInlay(element, sink, codePoint.getValue(), 0);
    }

    public final void addInlay(@NotNull V element, @NotNull InlayHintsSink sink, int codePoint) {
        addInlay(element, sink, codePoint, 0);
    }

    public final void addInlay(@NotNull V element, @NotNull InlayHintsSink sink, @NotNull CESymbol codePoint, int modifier) {
        addInlay(element, sink, codePoint.getValue(), modifier, true);
    }

    public final void addInlay(@NotNull V element, @NotNull InlayHintsSink sink, int codePoint, int modifier) {
        addInlay(element, sink, codePoint, modifier, true);
    }

    public final void addInlay(@NotNull V element, @NotNull InlayHintsSink sink, @NotNull CESymbol codePoint, int modifier, boolean addColor) {
        addInlay(element, sink, codePoint.getValue(), modifier, true);
    }

    public void addInlay(@NotNull V element, @NotNull InlayHintsSink sink, int codePoint, int modifier, boolean addColor) {
        InlayPresentation inlay = configureInlay(codePoint, modifier, addColor);
        sink.addInlineElement(element.getTextOffset() + element.getTextLength(), false, inlay, false);
    }

    private @NotNull InlayPresentation configureInlay(int codePoint, int modifier, boolean addColor) {
        PresentationFactory factory = getFactory();
        var inlay = factory.text(CEUtil.generateEmoji(codePoint, modifier, addColor));
        inlay = factory.roundWithBackgroundAndSmallInset(inlay);
        String tooltip = getTooltip();
        if (tooltip != null) {
            inlay = factory.withTooltip(tooltip, inlay);
        }
        return inlay;
    }

    public abstract void processInlay(T element, InlayHintsSink sink);
}