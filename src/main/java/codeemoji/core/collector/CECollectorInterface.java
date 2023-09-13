package codeemoji.core.collector;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public sealed interface CECollectorInterface<A extends PsiElement> extends InlayHintsCollector permits CECollector {

    Editor getEditor();

    void addInlayInline(@Nullable A element, InlayHintsSink sink, InlayPresentation inlay);

    void addInlayBlock(@Nullable A element, @NotNull InlayHintsSink sink, InlayPresentation inlay);

    @SuppressWarnings("SameReturnValue")
    boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink);

    default boolean collect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (this.isEnabled()) {
            return this.processCollect(psiElement, editor, inlayHintsSink);
        }
        return false;
    }

    default int calcOffset(@Nullable final A element) {
        if (null != element) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }

    default boolean isEnabled() {
        return true;
    }

}