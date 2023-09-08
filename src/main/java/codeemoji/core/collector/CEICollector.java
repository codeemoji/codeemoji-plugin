package codeemoji.core.collector;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public sealed interface CEICollector<A extends PsiElement> extends InlayHintsCollector permits CECollector {

    Editor getEditor();

    void addInlayInline(@Nullable A element, InlayHintsSink sink, InlayPresentation inlay);

    void addInlayBlock(@Nullable A element, @NotNull InlayHintsSink sink, InlayPresentation inlay);

    boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink);

    default boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (isEnabled()) {
            return processCollect(psiElement, editor, inlayHintsSink);
        }
        return false;
    }

    default int calcOffset(@Nullable A element) {
        if (element != null) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }

    default boolean isEnabled() {
        return true;
    }

}