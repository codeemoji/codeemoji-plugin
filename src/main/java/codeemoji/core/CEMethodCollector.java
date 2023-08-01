package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public abstract class CEMethodCollector extends CECollector<PsiMethod, PsiIdentifier> {

    public CEMethodCollector(Editor editor, String keyId) {
        super(editor, keyId);
    }

    public CEMethodCollector(Editor editor, String keyId, CEInlay ceInlay) {
        super(editor, keyId, ceInlay);
    }

    public CEMethodCollector(Editor editor, String keyId, int codePoint) {
        super(editor, keyId, codePoint);
    }

    public CEMethodCollector(Editor editor, String keyId, CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitMethod(@NotNull PsiMethod method) {
                    if (isHintable(method)) {
                        addInlayOnEditor(method.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitMethod(method);
                }
            });
        }
        return false;
    }
}