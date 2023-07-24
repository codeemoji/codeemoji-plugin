package codeemoji.core;

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class CodeemojiCollector extends FactoryInlayHintsCollector {

    public CodeemojiCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public final boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        if (CodeemojiUtil.isPreviewEditor(editor)) {
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


    private boolean collectForPreviewEditor(PsiElement element, InlayHintsSink sink) {
        if (element instanceof PsiMethod method) {
            processInlayHint(method, sink);
        }
        return false;
    }

    public @NotNull InlayPresentation configureInlayHint(String tooltip, int codePoint, boolean addColor) {
        return CodeemojiUtil.configureInlayHint(getFactory(), tooltip, codePoint, addColor);
    }

    public abstract void processInlayHint(@Nullable PsiMethod method, InlayHintsSink sink);
}