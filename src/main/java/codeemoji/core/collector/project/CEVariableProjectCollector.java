package codeemoji.core.collector.project;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CEVariableProjectCollector extends CEProjectCollector {

    public CEVariableProjectCollector(@NotNull Editor editor) {
        super(editor);
    }

    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        return false;
    }

}