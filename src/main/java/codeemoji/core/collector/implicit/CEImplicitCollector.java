package codeemoji.core.collector.implicit;

import codeemoji.core.collector.CECollector;
import codeemoji.core.util.CESymbol;
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

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract class CEImplicitCollector extends CECollector<PsiElement> {

    public final @NotNull String keyId;
    public final int codePoint;

    protected CEImplicitCollector(@NotNull Editor editor, @NotNull String keyId, int codePoint) {
        super(editor);
        this.keyId = keyId;
        this.codePoint = codePoint;
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

                private boolean hasImplicitBase(@Nullable JvmAnnotatedElement clazz) {
                    if (null != clazz) {
                        return null != clazz.getAnnotation(getBaseName());
                    }
                    return false;
                }
            });
        }
        return false;
    }

    protected void addInlayInAnnotation(@Nullable PsiAnnotation annotation, @NotNull InlayHintsSink sink, @NotNull InlayPresentation inlay) {
        if (null != annotation) {
            sink.addInlineElement(calcOffsetForAnnotation(annotation), false, inlay, false);
        }
    }

    protected void addInlayInAttribute(@Nullable PsiAnnotation annotation, @Nullable String attributeName, @NotNull InlayHintsSink sink, @NotNull InlayPresentation inlay) {
        if (null != annotation && null != attributeName) {
            sink.addInlineElement(calcOffsetForAttribute(annotation, attributeName), false, inlay, false);
        }
    }

    private int calcOffsetForAnnotation(@NotNull PsiAnnotation annotation) {
        var result = annotation.getTextOffset();
        var attributes = annotation.getAttributes();
        if (attributes.isEmpty() && !annotation.getText().contains("(") && !annotation.getText().contains(")")) {
            result += annotation.getTextLength();
        } else {
            result += annotation.getTextLength() - 1;
        }
        return result;
    }

    private int calcOffsetForAttribute(@NotNull PsiAnnotation annotation, @NotNull String attributeName) {
        var attributeValue = annotation.findAttributeValue(attributeName);
        var result = 0;
        if (null != attributeValue) {
            result = attributeValue.getTextOffset() + attributeValue.getTextLength() - 1;
        }
        return result;
    }

    protected void processImplicitsList(@NotNull PsiMember member, @NotNull Iterable<? extends CEImplicitInterface> implicits, @NotNull InlayHintsSink sink) {
        for (var implicit : implicits) {
            var hasImplicitAnnotation = false;
            var annotation = member.getAnnotation(implicit.getBaseName());
            if (null != annotation) {
                for (var attribute : annotation.getAttributes()) {
                    var attributeName = attribute.getAttributeName();
                    var valueComplement = implicit.updateAttributesFor(member, annotation, attributeName);
                    addImplicitInlayForAttributeValue(annotation, attributeName, valueComplement, sink);
                }
                var newAttributesList = implicit.createAttributesFor(member, annotation);
                addImplicitInlayForAnnotation(annotation, newAttributesList, sink);
                hasImplicitAnnotation = true;
            }
            if (!hasImplicitAnnotation) {
                var complement = implicit.createAnnotationFor(member);
                if (null != complement) {
                    addImplicitInlay(member, complement, sink);
                }
            }
        }
    }

    private void addImplicitInlayForAttributeValue(PsiAnnotation annotation, @Nullable String attributeName, @Nullable String attributeValue, InlayHintsSink sink) {
        if (null != attributeValue) {
            var inlay = buildInlayWithText(attributeValue, "inlay." + keyId + ".attributes.tooltip", null);
            addInlayInAttribute(annotation, attributeName, sink, inlay);
        }
    }

    private void addImplicitInlayForAnnotation(PsiAnnotation annotation, @Nullable String newAttributesList, @NotNull InlayHintsSink sink) {
        if (null != newAttributesList) {
            var inlay = buildInlayWithText(newAttributesList, "inlay." + keyId + ".attributes.tooltip", null);
            addInlayInAnnotation(annotation, sink, inlay);
        }
    }

    private void addImplicitInlay(PsiElement element, @Nullable String fullText, @NotNull InlayHintsSink sink) {
        if (null != fullText) {
            var symbol = new CESymbol(codePoint, fullText);
            var inlay = buildInlayWithEmoji(symbol, "inlay." + keyId + ".annotations.tooltip", null);
            addInlayBlock(element, sink, inlay);
        }
    }

    protected abstract String getBaseName();

    protected abstract void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink);
}