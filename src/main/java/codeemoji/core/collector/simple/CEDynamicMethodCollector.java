package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract non-sealed class CEDynamicMethodCollector extends CESimpleCollector<PsiMethod, PsiIdentifier> {

    protected CEDynamicMethodCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public final boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitMethod(@NotNull PsiMethod method) {
                    if (needsHint(method, processExternalInfo(method))) {
                        inlay = updateInlay();
                        addInlay(method.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitMethod(method);
                }
            });
        }
        return false;
    }

    public abstract InlayPresentation updateInlay();
}

