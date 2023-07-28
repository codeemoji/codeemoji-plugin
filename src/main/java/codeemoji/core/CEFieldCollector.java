package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;

public abstract class CEFieldCollector extends CECollector<PsiField, PsiIdentifier> {

    public CEFieldCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId);
    }

    @Override
    public boolean collectForPreviewEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) throws RuntimeException {
        if (element instanceof PsiField field) {
            execute(field, sink);
        }
        return false;
    }

    @Override
    public boolean collectForRegularEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) throws RuntimeException {
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitField(@NotNull PsiField field) {
                execute(field, sink);
                super.visitField(field);
            }
        });
        return false;
    }

}