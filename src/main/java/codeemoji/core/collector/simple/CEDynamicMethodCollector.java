package codeemoji.core.collector.simple;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public abstract non-sealed class CEDynamicMethodCollector extends CESimpleDynamicCollector<PsiMethod, PsiIdentifier> {

    protected CEDynamicMethodCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public final boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitMethod(@NotNull PsiMethod method) {
                    InlayPresentation dynamicInlay = needsHint(method, processExternalInfo(method));
                    if (dynamicInlay != null) {
                        inlay = dynamicInlay;
                        addInlay(method.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitMethod(method);
                }
            });
        }
        return false;
    }
}

