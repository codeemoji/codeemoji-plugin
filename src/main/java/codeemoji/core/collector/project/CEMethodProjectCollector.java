package codeemoji.core.collector.project;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CEMethodProjectCollector extends CEProjectCollector {

    public CEMethodProjectCollector(@NotNull Editor editor) {
        super(editor);
    }

    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        return false;
    }

}