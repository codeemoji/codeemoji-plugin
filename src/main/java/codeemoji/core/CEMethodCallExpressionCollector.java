package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEMethodCallExpressionCollector extends CECollector<PsiMethod, PsiMethodCallExpression> {

    public CEMethodCallExpressionCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId, new CESymbol());
    }

    public CEMethodCallExpressionCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitCallExpression(@NotNull PsiCallExpression callExpression) {
                    if (callExpression instanceof PsiMethodCallExpression mexp) {
                        PsiMethod method = mexp.resolveMethod();
                        if (method != null) {
                            if (isHintable(method)) {
                                addInlayOnEditor(mexp, inlayHintsSink);
                            }
                        }
                    }
                    super.visitCallExpression(callExpression);
                }

            });
        }
        return false;
    }

    @Override
    public int calcOffset(@Nullable PsiMethodCallExpression element) {
        if (element != null) {
            return element.getTextOffset() + element.getMethodExpression().getTextLength();
        }
        return 0;
    }
}