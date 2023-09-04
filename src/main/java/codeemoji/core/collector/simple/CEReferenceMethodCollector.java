package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEReferenceMethodCollector extends CESimpleCollector<PsiMethod, PsiMethodCallExpression> {

    protected CEReferenceMethodCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public final boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitCallExpression(@NotNull PsiCallExpression callExpression) {
                    if (CEUtils.isNotPreviewEditor(editor) &&
                            (callExpression instanceof PsiMethodCallExpression mexp)) {
                        var method = mexp.resolveMethod();
                        if (method != null && (needsHint(method))) {
                            addInlay(mexp, inlayHintsSink);

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