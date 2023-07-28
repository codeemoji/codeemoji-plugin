package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public abstract class CEMethodCollector extends CECollector<PsiMethod, PsiIdentifier> {

    public CEMethodCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId);
    }

    @Override
    public boolean collectForPreviewEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) {
        if (element instanceof PsiMethod method) {
            execute(method, sink);
        }
        return false;
    }

    @Override
    public boolean collectForRegularEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) {
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                execute(method, sink);
                super.visitMethod(method);
            }
        });
        return false;
    }
}