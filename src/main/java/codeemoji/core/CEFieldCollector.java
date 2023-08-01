package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public abstract class CEFieldCollector extends CECollector<PsiField, PsiIdentifier> {

    public CEFieldCollector(Editor editor, String keyId) {
        super(editor, keyId);
    }

    public CEFieldCollector(Editor editor, String keyId, CEInlay ceInlay) {
        super(editor, keyId, ceInlay);
    }

    public CEFieldCollector(Editor editor, String keyId, int codePoint) {
        super(editor, keyId, codePoint);
    }

    public CEFieldCollector(Editor editor, String keyId, CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull PsiField field) {
                    if (isHintable(field)) {
                        addInlayOnEditor(field.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitField(field);
                }
            });
        }
        return false;
    }
}