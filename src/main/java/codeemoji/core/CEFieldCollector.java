package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
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
    public final boolean collectInPreviewEditor(PsiElement element, InlayHintsSink sink) {
        if (element instanceof PsiField field) {
            checkAddInlay(field);
        }
        return false;
    }

    @Override
    public final boolean collectInDefaultEditor(@NotNull PsiElement element, InlayHintsSink sink) {
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitField(@NotNull PsiField field) {
                if (checkAddInlay(field)) {
                    addInlayOnEditor(field.getNameIdentifier(), sink);
                }
                super.visitField(field);
            }
        });
        return false;
    }

}