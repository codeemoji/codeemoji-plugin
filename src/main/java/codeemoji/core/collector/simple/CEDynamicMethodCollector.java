package codeemoji.core.collector.simple;

import codeemoji.core.collector.CECollector;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

//TODO: merge with superclass
//can i merge this with superclass?
public abstract class CEDynamicMethodCollector extends CECollector<PsiMethod, PsiIdentifier> {

    protected CEDynamicMethodCollector(@NotNull Editor editor, String key) {
        super(editor, key);
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                var inlay = createInlayFor(method);
                if (inlay != null) {
                    addInlayInline(method.getNameIdentifier(), InlayTreeSink, inlay);
                }
                super.visitMethod(method);
            }
        };
    }


}

