package codeemoji.core.collector;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public record CECollectorMulti(List<InlayHintsCollector> collectors) implements InlayHintsCollector {

    @Override
    public boolean collect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (null != this.collectors()) {
            for (final var collector : this.collectors()) {
                collector.collect(psiElement, editor, inlayHintsSink);
            }
        }
        return false;
    }
}