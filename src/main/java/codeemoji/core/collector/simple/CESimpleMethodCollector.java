package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CESimpleMethodCollector extends CESimpleCollector<PsiMethod, PsiIdentifier> {

    protected CESimpleMethodCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, @Nullable CESymbol symbol) {
        super(editor, key, symbol);
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                var inlay = createInlayFor(method);
                if (inlay != null) {
                    addInlayInline(method.getNameIdentifier(), inlayHintsSink, inlay);
                }
                super.visitMethod(method);
            }
        };
    }
}