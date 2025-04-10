package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract non-sealed class CESimpleClassCollector extends CESimpleCollector<PsiClass, PsiIdentifier> {

    @SuppressWarnings("unused")
    protected CESimpleClassCollector(@NotNull Editor editor, String key, Supplier<CESymbol> settings) {
        super(editor, key, settings);
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitClass(@NotNull PsiClass aClass) {
                if (aClass instanceof PsiTypeParameter) {
                    return; // Skip generics
                }
                var inlay = createInlayFor(aClass);
                if (inlay != null) {
                    addInlayInline(aClass.getNameIdentifier(), InlayTreeSink, inlay);
                }
                super.visitClass(aClass);
            }
        };
    }
}