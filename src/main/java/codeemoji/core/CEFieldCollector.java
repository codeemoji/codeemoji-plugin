package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;

public abstract class CEFieldCollector extends CECollector<PsiField, PsiIdentifier> {

    String keyId = getClass().getSimpleName().toLowerCase();

    public CEFieldCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId);
    }

    @Override
    public final boolean collectInPreviewEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) {
        if (element instanceof PsiField field) {
            processInlay(field, sink);
        }
        return false;
    }

    @Override
    public final boolean collectInDefaultEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) {
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitField(@NotNull PsiField field) {
                processInlay(field, sink);
                super.visitField(field);
            }
        });
        return false;
    }

}