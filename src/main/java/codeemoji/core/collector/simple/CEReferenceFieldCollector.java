package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEReferenceFieldCollector extends CESimpleCollector<PsiField, PsiReferenceExpression> {

    protected CEReferenceFieldCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key,
                                        @NotNull String tooltipKey,
                                        @Nullable CESymbol symbol) {
        super(editor, key, tooltipKey, symbol);
    }

    @Override
    public final boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        var reference = expression.getReference();
                        if (null != reference) {
                            var resolveElement = reference.resolve();
                            if (resolveElement instanceof PsiField field && needsHint(field, processExternalInfo(field))) {
                                addInlay(expression, inlayHintsSink);
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
    public int calcOffset(@Nullable PsiReferenceExpression reference) {
        if (null != reference) {
            var lastChild = reference.getLastChild();
            var length = lastChild.getTextLength();
            return lastChild.getTextOffset() + length;
        }
        return 0;
    }
}