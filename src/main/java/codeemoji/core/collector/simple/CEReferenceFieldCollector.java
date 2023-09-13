package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEReferenceFieldCollector extends CECollectorSimple<PsiField, PsiReferenceExpression> {

    protected CEReferenceFieldCollector(@NotNull final Editor editor, @NotNull final String keyId, @Nullable final CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public final boolean processCollect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull final PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        final var reference = expression.getReference();
                        if (null != reference) {
                            final var resolveElement = reference.resolve();
                            if (resolveElement instanceof final PsiField field && CEReferenceFieldCollector.this.needsHint(field)) {
                                CEReferenceFieldCollector.this.addInlay(expression, inlayHintsSink);
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }
            });
        }
        return false;
    }

    @Override
    public int calcOffset(@Nullable final PsiReferenceExpression reference) {
        if (null != reference) {
            final var lastChild = reference.getLastChild();
            final var length = lastChild.getTextLength();
            return lastChild.getTextOffset() + length;
        }
        return 0;
    }
}