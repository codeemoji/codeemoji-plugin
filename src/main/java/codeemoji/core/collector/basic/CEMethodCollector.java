package codeemoji.core.collector.basic;

import codeemoji.core.collector.CESingleCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract class CEMethodCollector extends CESingleCollector<PsiMethod, PsiIdentifier> {

    protected CEMethodCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitMethod(@NotNull PsiMethod method) {
                    if (needsHint(method)) {
                        addInlay(method.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitMethod(method);
                }
            });
        }
        return false;
    }
}