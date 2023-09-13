package codeemoji.core.collector;

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
public abstract non-sealed class CECollector<A extends PsiElement> extends CEInlayProcessor implements CECollectorInterface<A> {

    protected CECollector(final Editor editor) {
        super(editor);
    }

    @Override
    public final void addInlayInline(@Nullable final A element, @NotNull final InlayHintsSink sink, @NotNull final InlayPresentation inlay) {
        if (null != element) {
            sink.addInlineElement(this.calcOffset(element), false, inlay, false);
        }
    }

    @Override
    public final void addInlayBlock(@Nullable final A element, @NotNull final InlayHintsSink sink, InlayPresentation inlay) {
        if (null != element) {
            final var indentFactor = EditorUtil.getPlainSpaceWidth(this.getEditor());
            final var indent = EditorUtil.getTabSize(this.getEditor()) * indentFactor;
            inlay = this.getFactory().inset(inlay, indent, 0, 0, 0);
            sink.addBlockElement(element.getTextOffset(), true, true, 0, inlay);
        }
    }

}