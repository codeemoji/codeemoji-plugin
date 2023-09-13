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

    protected CECollector(Editor editor) {
        super(editor);
    }

    @Override
    public void addInlayInline(@Nullable A element, @NotNull InlayHintsSink sink, @NotNull InlayPresentation inlay) {
        if (element != null) {
            sink.addInlineElement(calcOffset(element), false, inlay, false);
        }
    }

    @Override
    public void addInlayBlock(@Nullable A element, @NotNull InlayHintsSink sink, InlayPresentation inlay) {
        if (element != null) {
            var indentFactor = EditorUtil.getPlainSpaceWidth(getEditor());
            var indent = EditorUtil.getTabSize(getEditor()) * indentFactor;
            inlay = getFactory().inset(inlay, indent, 0, 0, 0);
            sink.addBlockElement(element.getTextOffset(), true, true, 0, inlay);
        }
    }

}