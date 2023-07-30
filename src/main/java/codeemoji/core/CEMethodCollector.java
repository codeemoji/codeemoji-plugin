package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
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
    public final boolean collectInPreviewEditor(PsiElement element, InlayHintsSink sink) {
        if (element instanceof PsiMethod method) {
            if (checkAddInlay(method)) {
                addInlayOnEditor(method.getNameIdentifier(), sink);
            }
        }
        return false;
    }

    @Override
    public final boolean collectInDefaultEditor(@NotNull PsiElement element, InlayHintsSink sink) {
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                if (checkAddInlay(method)) {
                    addInlayOnEditor(method.getNameIdentifier(), sink);
                }
                super.visitMethod(method);
            }
        });
        return false;
    }
}