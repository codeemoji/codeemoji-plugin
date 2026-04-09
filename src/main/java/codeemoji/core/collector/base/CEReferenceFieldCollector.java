package codeemoji.core.collector.base;

import codeemoji.core.collector.CECollector;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEReferenceFieldCollector extends CECollector<PsiField, PsiReferenceExpression> {

    protected CEReferenceFieldCollector(@NotNull Editor editor, String key) {
        super(editor, key);
    }


    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                if (CEUtils.isNotPreviewEditor(editor)) {
                    var reference = expression.getReference();
                    if (null != reference) {
                        var resolveElement = reference.resolve();
                        if (resolveElement instanceof PsiField field) {
                            var inlay = createInlayFor(field);
                            if (inlay != null) {
                                addInlayInline(expression, InlayTreeSink, inlay);
                            }
                        }
                    }
                }
                super.visitReferenceExpression(expression);
            }
        };
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
