package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public abstract class CEParameterCollector extends CECollector<PsiParameter, PsiIdentifier> {

    public CEParameterCollector(Editor editor, String keyId) {
        super(editor, keyId);
    }

    public CEParameterCollector(Editor editor, String keyId, CEInlay ceInlay) {
        super(editor, keyId, ceInlay);
    }

    public CEParameterCollector(Editor editor, String keyId, int codePoint) {
        super(editor, keyId, codePoint);
    }

    public CEParameterCollector(Editor editor, String keyId, CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitParameter(@NotNull PsiParameter parameter) {
                    if (isHintable(parameter)) {
                        addInlayOnEditor(parameter.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitParameter(parameter);
                }
            });
        }
        return false;
    }
}