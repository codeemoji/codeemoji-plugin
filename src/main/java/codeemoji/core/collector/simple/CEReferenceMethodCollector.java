package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEReferenceMethodCollector extends CESimpleCollector<PsiMethod, PsiMethodCallExpression> {

    protected CEReferenceMethodCollector(@NotNull Editor editor, String key,
                                         @Nullable CESymbol symbol) {
        this(editor, key, key, symbol);
    }
    protected CEReferenceMethodCollector(@NotNull Editor editor, String key,
                                         @NotNull String tooltipId, @Nullable CESymbol symbol) {
        super(editor, key, tooltipId, symbol);
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitCallExpression(@NotNull PsiCallExpression callExpression) {
                if (CEUtils.isNotPreviewEditor(editor) &&
                        (callExpression instanceof PsiMethodCallExpression mexp)) {
                    var method = mexp.resolveMethod();
                    if (null != method) {
                        var inlay = createInlayFor(method);
                        if (inlay != null) {
                            addInlayInline(mexp, InlayTreeSink, inlay);
                        }
                    }

                }
                super.visitCallExpression(callExpression);
            }
        };
    }

    @Override
    public int calcOffset(@Nullable PsiMethodCallExpression element) {
        if (null != element) {
            return element.getTextOffset() + element.getMethodExpression().getTextLength();
        }
        return 0;
    }
}