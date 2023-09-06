package codeemoji.core.collector;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CECollectorInline<A extends PsiElement> extends CEInlayProcessor implements CEICollector<A> {

    protected CECollectorInline(Editor editor) {
        super(editor);
    }

    @Override
    public void addInlay(@Nullable A element, InlayHintsSink sink, InlayPresentation inlay) {
        if (element != null) {
            sink.addInlineElement(calcOffset(element), false, inlay, false);
        }
    }

}