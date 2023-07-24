package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class CEMethodCollector extends CECollector {

    public CEMethodCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        if (CEUtil.isPreviewEditor(editor)) {
            return collectForPreviewEditor(element, sink);
        }
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                processInlayHint(method, sink);
                super.visitMethod(method);
            }
        });
        return false;
    }

    @Override
    public boolean collectForPreviewEditor(PsiElement element, InlayHintsSink sink) {
        if (element instanceof PsiMethod method) {
            processInlayHint(method, sink);
        }
        return false;
    }

    public void addInlayHint(@Nullable PsiElement element, @NotNull InlayHintsSink sink, InlayPresentation inlay) {
        if (element instanceof PsiMethod method) {
            PsiIdentifier identifier = method.getNameIdentifier();
            if (identifier != null) {
                sink.addInlineElement(identifier.getTextOffset() + identifier.getTextLength(), false, inlay, false);
            }
        }
    }

    public final void processInlayHint(@Nullable PsiElement element, InlayHintsSink sink) {
        if (element instanceof PsiMethod method) {
            processInlayHint(method, sink);
        }
    }

    public abstract void processInlayHint(PsiMethod method, InlayHintsSink sink);


}