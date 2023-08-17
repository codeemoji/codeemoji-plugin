package codeemoji.core.collector;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class CEMultiCollector extends FactoryInlayHintsCollector {

    private final List<InlayHintsCollector> collectors;

    public CEMultiCollector(Editor editor, List<InlayHintsCollector> collectors) {
        super(editor);
        this.collectors = collectors;
    }

    @Override
    public final boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (getCollectors() != null) {
            for (InlayHintsCollector collector : getCollectors()) {
                collector.collect(psiElement, editor, inlayHintsSink);
            }
        }
        return false;
    }
}