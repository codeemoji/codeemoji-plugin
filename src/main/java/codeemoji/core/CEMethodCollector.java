package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

public abstract class CEMethodCollector extends CECollector<PsiMethod, PsiIdentifier> {

    public CEMethodCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public final boolean collectInPreviewEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) throws RuntimeException {
        if (element instanceof PsiMethod method) {
            processInlay(method, sink);
        }
        return false;
    }

    @Override
    public final boolean collectInDefaultEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) throws RuntimeException {
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                processInlay(method, sink);
                super.visitMethod(method);
            }
        });
        return false;
    }
}