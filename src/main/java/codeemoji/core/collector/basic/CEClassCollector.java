package codeemoji.core.collector.basic;

import codeemoji.core.collector.CESingleCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEClassCollector extends CESingleCollector<PsiClass, PsiIdentifier> {

    protected CEClassCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitClass(@NotNull PsiClass clazz) {
                    if (checkHint(clazz)) {
                        addInlay(clazz.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitClass(clazz);
                }
            });
        }
        return false;
    }
}