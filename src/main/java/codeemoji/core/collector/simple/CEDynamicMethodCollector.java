package codeemoji.core.collector.simple;

import codeemoji.core.collector.CECollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

//can i merge this with superclass?
public abstract class CEDynamicMethodCollector extends CECollector<PsiMethod, PsiIdentifier> {

    protected CEDynamicMethodCollector(@NotNull Editor editor, SettingsKey<?> settingsKey) {
        super(editor, settingsKey);
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

