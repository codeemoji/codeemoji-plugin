package codeemoji.core;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class CECollector extends FactoryInlayHintsCollector {

    public CECollector(@NotNull Editor editor) {
        super(editor);
    }

    public abstract boolean collectForPreviewEditor(PsiElement element, InlayHintsSink sink);

    public void addInlayHint(@Nullable PsiElement element, @NotNull InlayHintsSink sink, InlayPresentation inlay) {
        if (element != null) {
            sink.addInlineElement(element.getTextOffset() + element.getTextLength(), false, inlay, false);
        }
    }

    public @NotNull InlayPresentation configureInlayHint(int codePoint) {
        return CEUtil.configureInlayHint(getFactory(), null, codePoint, false);
    }

    public @NotNull InlayPresentation configureInlayHint(String tooltip, int codePoint) {
        return CEUtil.configureInlayHint(getFactory(), tooltip, codePoint, false);
    }

    public @NotNull InlayPresentation configureInlayHint(String tooltip, int codePoint, boolean addColor) {
        return CEUtil.configureInlayHint(getFactory(), tooltip, codePoint, addColor);
    }

    public abstract void processInlayHint(PsiElement element, InlayHintsSink sink);
}