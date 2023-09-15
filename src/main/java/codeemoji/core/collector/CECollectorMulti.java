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
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (null != collectors()) {
            for (var collector : collectors()) {
                collector.collect(psiElement, editor, inlayHintsSink);
            }
        }
        return false;
    }
}