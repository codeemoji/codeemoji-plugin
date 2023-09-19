package codeemoji.core.collector;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CECollector<A extends PsiElement> extends CEInlayBuilder implements InlayHintsCollector {

    protected CECollector(Editor editor) {
        super(editor);
    }

    @SuppressWarnings("SameReturnValue")
    public abstract boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink);

    public final boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (isEnabled()) {
            return processCollect(psiElement, editor, inlayHintsSink);
        }
        return false;
    }

    protected int calcOffset(@Nullable A element) {
        if (null != element) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }

    protected boolean isEnabled() {
        return true;
    }

    public final void addInlayInline(@Nullable A element, @NotNull InlayHintsSink sink, @NotNull InlayPresentation inlay) {
        if (null != element) {
            sink.addInlineElement(calcOffset(element), false, inlay, false);
        }
    }

    public final void addInlayBlock(@Nullable A element, @NotNull InlayHintsSink sink, InlayPresentation inlay) {
        if (null != element) {
            var indentFactor = EditorUtil.getPlainSpaceWidth(getEditor());
            var indent = EditorUtil.getTabSize(getEditor()) * indentFactor;
            inlay = getFactory().inset(inlay, indent, 0, 0, 0);
            sink.addBlockElement(element.getTextOffset(), true, true, 0, inlay);
        }
    }

}