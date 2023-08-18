package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEVariableProjectCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJvmModifiersOwner;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class VariableProjectCollector extends CEVariableProjectCollector {

    public VariableProjectCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public void checkHint(@NotNull PsiElement hintElement, @NotNull PsiJvmModifiersOwner evaluationElement, @NotNull InlayHintsSink sink) {

    }
}