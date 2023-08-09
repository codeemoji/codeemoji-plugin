package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEFieldReferenceCollector extends CECollector<PsiReferenceExpression, PsiReferenceExpression> {

    public CEFieldReferenceCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                    if (CEUtil.isNotPreviewEditor(editor)) {
                        if (isHintable(expression)) {
                            addInlayOnEditor(expression, inlayHintsSink);
                        }
                    } else {
                        //TODO: implement for preview editor
                    }
                    super.visitReferenceExpression(expression);
                }
            });
        }
        return false;
    }

    @Override
    public int calcOffset(@Nullable PsiReferenceExpression reference) {
        if (reference != null) {
            PsiElement lastChild = reference.getLastChild();
            int length = lastChild.getTextLength();
            return lastChild.getTextOffset() + length;
        }
        return 0;
    }
}