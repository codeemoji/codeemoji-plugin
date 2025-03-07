package codeemoji.core.collector;

import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CECollectorMulti(List<SharedBypassCollector> collectors) implements SharedBypassCollector {

    @Override
    public void collectFromElement(@NotNull PsiElement psiElement, @NotNull InlayTreeSink inlayTreeSink) {
        if (null != collectors()) {
            for (var collector : collectors()) {
                collector.collectFromElement(psiElement, inlayTreeSink);
            }
        }
    }
}