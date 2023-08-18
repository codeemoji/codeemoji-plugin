package codeemoji.core.collector.project;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJvmModifiersOwner;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class CEVariableProjectCollector extends CEProjectCollector<PsiJvmModifiersOwner, PsiElement> {

    protected CEVariableProjectCollector(@NotNull Editor editor) {
        super(editor);
    }

    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        return false;
    }

}