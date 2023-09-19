package codeemoji.core.collector.simple.reference;

import codeemoji.core.collector.simple.CESimpleCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract class CEReferenceFieldCollector extends CESimpleCollector<PsiField, PsiReferenceExpression> {

    protected CEReferenceFieldCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
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
                            if (resolveElement instanceof PsiField field && needsHint(field)) {
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