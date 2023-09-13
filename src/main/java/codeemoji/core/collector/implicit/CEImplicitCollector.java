package codeemoji.core.collector.implicit;

import codeemoji.core.collector.CECollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.lang.jvm.JvmAnnotatedElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
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
                    if (hasImplicitBase(clazz)) {
                        processImplicitsFor(clazz, inlayHintsSink);
                    }
                    super.visitClass(clazz);
                }

                @Override
                public void visitField(@NotNull final PsiField field) {
                    if (hasImplicitBase(field.getContainingClass())) {
                        processImplicitsFor(field, inlayHintsSink);
                    }
                    super.visitField(field);
                }

                @Override
                public void visitMethod(@NotNull final PsiMethod method) {
                    if (hasImplicitBase(method.getContainingClass())) {
                        processImplicitsFor(method, inlayHintsSink);
                    }
                    super.visitMethod(method);
                }

                private boolean hasImplicitBase(@Nullable final JvmAnnotatedElement clazz) {
                    if (null != clazz) {
                        for (final var bName : getBaseNames()) {
                            if (null != clazz.getAnnotation(bName)) {
                                return true;
                            }

                        }
                    }
                    return false;
                }
            });
        }
        return false;
    }

    protected void addInlayInAnnotation(@Nullable final PsiAnnotation annotation, @NotNull final InlayHintsSink sink, @NotNull final InlayPresentation inlay) {
        if (null != annotation) {
            sink.addInlineElement(this.calcOffsetForAnnotation(annotation), false, inlay, false);
        }
    }

    protected void addInlayInAttribute(@Nullable final PsiAnnotation annotation, @Nullable final String attributeName, @NotNull final InlayHintsSink sink, @NotNull final InlayPresentation inlay) {
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

    protected abstract List<String> getBaseNames();

    protected abstract void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink);
}