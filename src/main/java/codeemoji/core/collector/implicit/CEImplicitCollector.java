package codeemoji.core.collector.implicit;

import codeemoji.core.collector.CECollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract sealed class CEImplicitCollector extends CECollector<PsiElement>
        permits CEJPAEntityCollector {

    public CEImplicitCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {

                @Override
                public void visitClass(@NotNull PsiClass clazz) {
                    if (hasImplicitBase(clazz)) {
                        processImplicitsFor(clazz, inlayHintsSink);
                    }
                    super.visitClass(clazz);
                }

                @Override
                public void visitField(@NotNull PsiField field) {
                    if (hasImplicitBase(field.getContainingClass())) {
                        processImplicitsFor(field, inlayHintsSink);
                    }
                    super.visitField(field);
                }

                @Override
                public void visitMethod(@NotNull PsiMethod method) {
                    if (hasImplicitBase(method.getContainingClass())) {
                        processImplicitsFor(method, inlayHintsSink);
                    }
                    super.visitMethod(method);
                }
            });
        }
        return false;
    }

    public abstract boolean hasImplicitBase(@Nullable PsiClass containingClass);

    public abstract void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink);
}