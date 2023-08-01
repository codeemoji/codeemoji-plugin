package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public abstract class CEClassCollector extends CECollector<PsiClass, PsiIdentifier> {

    public CEClassCollector(Editor editor, String keyId) {
        super(editor, keyId);
    }

    public CEClassCollector(Editor editor, String keyId, CEInlay ceInlay) {
        super(editor, keyId, ceInlay);
    }

    public CEClassCollector(Editor editor, String keyId, int codePoint) {
        super(editor, keyId, codePoint);
    }

    public CEClassCollector(Editor editor, String keyId, CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitClass(@NotNull PsiClass clazz) {
                    if (isHintable(clazz)) {
                        addInlayOnEditor(clazz.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitClass(clazz);
                }
            });
        }
        return false;
    }
}