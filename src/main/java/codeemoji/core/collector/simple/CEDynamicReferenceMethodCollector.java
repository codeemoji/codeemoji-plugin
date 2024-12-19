package codeemoji.core.collector.simple;

import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEDynamicReferenceMethodCollector extends CESimpleDynamicCollector<PsiMethod, PsiMethodCallExpression> {

    protected CEDynamicReferenceMethodCollector(@NotNull Editor editor, SettingsKey<?> key) {
        super(editor, key);
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
                        if (null != method) {
                            InlayPresentation dynamicInlay = needsHint(method, processExternalInfo(method));
                            if (dynamicInlay != null) {
                                inlay = dynamicInlay;
                                addInlay(mexp, inlayHintsSink);
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
        if (null != element) {
            return element.getTextOffset() + element.getMethodExpression().getTextLength();
        }
        return 0;
    }
}
