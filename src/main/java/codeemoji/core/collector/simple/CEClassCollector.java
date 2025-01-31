package codeemoji.core.collector.simple;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract non-sealed class CEClassCollector extends CESimpleCollector<PsiClass, PsiIdentifier> {

    @SuppressWarnings("unused")
    protected CEClassCollector(@NotNull Editor editor, String key, Supplier<CEBaseSettings<?>> settings) {
        super(editor, key, settings);
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitClass(@NotNull PsiClass aClass) {
                var inlay = createInlayFor(aClass);
                if (inlay != null) {
                    addInlayInline(aClass.getNameIdentifier(), InlayTreeSink, inlay);
                }
                super.visitClass(aClass);
            }
        };
    }
}