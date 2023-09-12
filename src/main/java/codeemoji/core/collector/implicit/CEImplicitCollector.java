package codeemoji.core.collector.implicit;

import codeemoji.core.collector.CECollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract class CEImplicitCollector extends CECollector<PsiElement> {

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

    public void addInlayInAnnotation(@Nullable PsiAnnotation annotation, InlayHintsSink sink, InlayPresentation inlay) {
        if (annotation != null) {
            sink.addInlineElement(calcOffsetForAnnotation(annotation), false, inlay, false);
        }
    }

    int calcOffsetForAnnotation(@NotNull PsiAnnotation annotation) {
        var result = annotation.getTextOffset();
        var attributes = annotation.getAttributes();
        if (attributes.isEmpty() && !annotation.getText().contains("(") && !annotation.getText().contains(")")) {
            result += annotation.getTextLength();
        } else {
            result += annotation.getTextLength() - 1;
        }
        return result;
    }

    public boolean hasImplicitBase(@Nullable PsiClass containingClass) {
        if (containingClass != null) {
            for (var bName : getBaseNames()) {
                if (containingClass.getAnnotation(bName) != null) {
                    return true;
                }

            }
        }
        return false;
    }

    public abstract List<String> getBaseNames();

    public abstract void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink);
}