package codeemoji.core.collector;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class CESingleCollector<H extends PsiElement, A extends PsiElement> extends CECollector<A> {

    private final InlayPresentation inlay;

    public CESingleCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor);
        this.inlay = buildInlay(symbol, "inlay." + keyId + ".tooltip");
    }

    public void addInlayOnEditor(@Nullable A element, InlayHintsSink sink) {
        addInlayOnEditor(element, sink, getInlay());
    }

    public abstract boolean isHintable(@NotNull H element);
}