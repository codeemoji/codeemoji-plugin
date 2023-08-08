package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEMethodCollector extends CECollector<PsiMethod, PsiIdentifier> {

    public CEMethodCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId, new CESymbol());
    }

    public CEMethodCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
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