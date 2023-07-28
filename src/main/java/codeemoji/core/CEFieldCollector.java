package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEFieldCollector extends CECollector {

    public CEFieldCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId);
    }

    @Override
    public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        if (CEUtil.isPreviewEditor(editor)) {
            return collectForPreviewEditor(element, sink);
        }
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitField(@NotNull PsiField field) {
                processInlayHint(field, sink);
                super.visitField(field);
            }
        });
        return false;
    }

    @Override
    public boolean collectForPreviewEditor(PsiElement element, InlayHintsSink sink) {
        if (element instanceof PsiField field) {
            processInlayHint(field, sink);
        }
        return false;
    }

    @Override
    public void addInlayHint(@NotNull PsiElement element, @NotNull InlayHintsSink sink, int codePoint, int modifier, boolean addColor) {
        if (element instanceof PsiField field) {
            PsiIdentifier identifier = field.getNameIdentifier();
            super.addInlayHint(identifier, sink, codePoint, modifier, addColor);
        }
    }

    @Override
    public final void processInlayHint(@Nullable PsiElement element, InlayHintsSink sink) {
        if (element instanceof PsiField field) {
            processInlayHint(field, sink);
        }
    }

    public abstract void processInlayHint(@Nullable PsiField field, InlayHintsSink sink);
}