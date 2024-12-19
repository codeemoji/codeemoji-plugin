package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEClassCollector extends CESimpleCollector<PsiClass, PsiIdentifier> {

    @SuppressWarnings("unused")
    protected CEClassCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, @Nullable CESymbol symbol) {
        super(editor, key, symbol);
    }

    @Override
    public final boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitClass(@NotNull PsiClass aClass) {
                    if (needsHint(aClass, processExternalInfo(aClass))) {
                        addInlay(aClass.getNameIdentifier(), inlayHintsSink);
                    }
                    super.visitClass(aClass);
                }
            });
        }
        return false;
    }

}