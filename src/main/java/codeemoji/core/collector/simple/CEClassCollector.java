package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEClassCollector extends CESimpleCollector<PsiClass, PsiIdentifier> {

    @SuppressWarnings("unused")
    protected CEClassCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, @Nullable CESymbol symbol) {
        super(editor, key, symbol);
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitClass(@NotNull PsiClass aClass) {
                var inlay = createInlayFor(aClass);
                if (inlay != null) {
                    addInlayInline(aClass.getNameIdentifier(), inlayHintsSink, inlay);
                }
                super.visitClass(aClass);
            }
        };
    }
}