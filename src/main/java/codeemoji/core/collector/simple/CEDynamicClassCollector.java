package codeemoji.core.collector.simple;

import codeemoji.core.collector.CECollector;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public abstract class CEDynamicClassCollector extends CECollector<PsiClass, PsiIdentifier> {

    protected CEDynamicClassCollector(@NotNull Editor editor, String key) {
        super(editor, key);
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitClass(@NotNull PsiClass clazz) {
                if (clazz instanceof PsiTypeParameter) {
                    return; // Skip generics
                }

                var inlay = createInlayFor(clazz);
                if (inlay != null) {
                    addInlayInline(clazz.getNameIdentifier(), InlayTreeSink, inlay);
                }
                super.visitClass(clazz);
            }
        };
    }


}

