package codeemoji.core.collector;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class CECollectorImpl<A extends PsiElement> extends FactoryInlayHintsCollector implements ICECollector<A> {

    private final Editor editor;

    protected CECollectorImpl(@NotNull Editor editor) {
        super(editor);
        this.editor = editor;
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (isEnabled()) {
            return processCollect(psiElement, editor, inlayHintsSink);
        }
        return false;
    }
}