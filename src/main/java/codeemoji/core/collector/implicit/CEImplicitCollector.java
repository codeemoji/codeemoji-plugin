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

    protected CEImplicitCollector(@NotNull final Editor editor) {
        super(editor);
    }

    @Override
    public boolean processCollect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitClass(@NotNull final PsiClass clazz) {
                    if (CEImplicitCollector.this.hasImplicitBase(clazz)) {
                        CEImplicitCollector.this.processImplicitsFor(clazz, inlayHintsSink);
                    }
                    super.visitClass(clazz);
                }

                @Override
                public void visitField(@NotNull final PsiField field) {
                    if (CEImplicitCollector.this.hasImplicitBase(field.getContainingClass())) {
                        CEImplicitCollector.this.processImplicitsFor(field, inlayHintsSink);
                    }
                    super.visitField(field);
                }

                @Override
                public void visitMethod(@NotNull final PsiMethod method) {
                    if (CEImplicitCollector.this.hasImplicitBase(method.getContainingClass())) {
                        CEImplicitCollector.this.processImplicitsFor(method, inlayHintsSink);
                    }
                    super.visitMethod(method);
                }
            });
        }
        return false;
    }

    public void addInlayInAnnotation(@Nullable final PsiAnnotation annotation, @NotNull final InlayHintsSink sink, @NotNull final InlayPresentation inlay) {
        if (null != annotation) {
            sink.addInlineElement(this.calcOffsetForAnnotation(annotation), false, inlay, false);
        }
    }

    public void addInlayInAttribute(@Nullable final PsiAnnotation annotation, @Nullable final String attributeName, @NotNull final InlayHintsSink sink, @NotNull final InlayPresentation inlay) {
        if (null != annotation && null != attributeName) {
            sink.addInlineElement(this.calcOffsetForAttribute(annotation, attributeName), false, inlay, false);
        }
    }

    private int calcOffsetForAnnotation(@NotNull final PsiAnnotation annotation) {
        var result = annotation.getTextOffset();
        final var attributes = annotation.getAttributes();
        if (attributes.isEmpty() && !annotation.getText().contains("(") && !annotation.getText().contains(")")) {
            result += annotation.getTextLength();
        } else {
            result += annotation.getTextLength() - 1;
        }
        return result;
    }

    private int calcOffsetForAttribute(@NotNull final PsiAnnotation annotation, @NotNull final String attributeName) {
        final var attributeValue = annotation.findAttributeValue(attributeName);
        var result = 0;
        if (null != attributeValue) {
            result = attributeValue.getTextOffset() + attributeValue.getTextLength() - 1;
        }
        return result;
    }

    public boolean hasImplicitBase(@Nullable final PsiClass clazz) {
        if (null != clazz) {
            for (final var bName : this.getBaseNames()) {
                if (null != clazz.getAnnotation(bName)) {
                    return true;
                }

            }
        }
        return false;
    }

    public abstract List<String> getBaseNames();

    public abstract void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink);
}