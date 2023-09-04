package codeemoji.core.collector;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public sealed interface CEICollector<A extends PsiElement> extends InlayHintsCollector permits CECollector {

    default PresentationFactory getFactory() {
        return new PresentationFactory(getEditor());
    }

    Editor getEditor();

    boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink);

    void addInlay(@Nullable A element, InlayHintsSink sink, InlayPresentation inlay);

    InlayPresentation buildInlay(@Nullable CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip);

    int calcOffset(@Nullable A element);

    boolean isEnabled();

}