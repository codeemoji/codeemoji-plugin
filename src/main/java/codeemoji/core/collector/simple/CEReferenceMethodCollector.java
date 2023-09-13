package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEReferenceMethodCollector extends CECollectorSimple<PsiMethod, PsiMethodCallExpression> {

    protected CEReferenceMethodCollector(@NotNull final Editor editor, @NotNull final String keyId, @Nullable final CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public final boolean processCollect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitCallExpression(@NotNull final PsiCallExpression callExpression) {
                    if (CEUtils.isNotPreviewEditor(editor) &&
                            (callExpression instanceof final PsiMethodCallExpression mexp)) {
                        final var method = mexp.resolveMethod();
                        if (null != method && (CEReferenceMethodCollector.this.needsHint(method))) {
                            CEReferenceMethodCollector.this.addInlay(mexp, inlayHintsSink);

                        }

                    }
                    super.visitCallExpression(callExpression);
                }

            });
        }
        return false;
    }

    @Override
    public int calcOffset(@Nullable final PsiMethodCallExpression element) {
        if (null != element) {
            return element.getTextOffset() + element.getMethodExpression().getTextLength();
        }
        return 0;
    }
}