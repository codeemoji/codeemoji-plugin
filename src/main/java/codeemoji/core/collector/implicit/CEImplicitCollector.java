package codeemoji.core.collector.implicit;

import codeemoji.core.collector.CECollector;
import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.external.scanners.OSVVulnerabilityScanner;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.lang.jvm.JvmAnnotatedElement;
import com.intellij.lang.jvm.JvmAnnotation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
//TODO: fix generics
public abstract class CEImplicitCollector extends CECollector {

    public final int codePoint;

    protected CEImplicitCollector(@NotNull Editor editor, String key, int codePoint) {
        super(editor, key);
        this.codePoint = codePoint;
    }

    // No-op
    @Override
    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitClass(@NotNull PsiClass aClass) {
                if (hasImplicitBase(aClass)) {
                    processImplicitsFor(aClass, InlayTreeSink);
                }
                super.visitClass(aClass);
            }

            @Override
            public void visitField(@NotNull PsiField field) {
                if (hasImplicitBase(field.getContainingClass())) {
                    processImplicitsFor(field, InlayTreeSink);
                }
                super.visitField(field);
            }

            @Override
            public void visitMethod(@NotNull PsiMethod method) {
                if (hasImplicitBase(method.getContainingClass())) {
                    processImplicitsFor(method, InlayTreeSink);
                }
                super.visitMethod(method);
            }

            private boolean hasImplicitBase(@Nullable JvmAnnotatedElement clazz) {
                if (null != clazz) {
                    return null != clazz.getAnnotation(getBaseName()) && !isDeactivatedFor(clazz);
                }
                return false;
            }
        };
    }

    protected final void addInlayInAnnotation(@Nullable PsiAnnotation annotation, @NotNull InlayTreeSink sink, @NotNull InlayVisuals inlay) {
        if (null != annotation) {
            //TODO: add back
            //sink.addInlineElement(calcOffsetForAnnotation(annotation), false, text, false);
        }
    }

    protected final void addInlayInAttribute(@Nullable PsiAnnotation annotation, @Nullable String attributeName,
                                             @NotNull InlayTreeSink sink, @NotNull InlayVisuals inlay, int shiftOffset) {
        if (null != annotation && null != attributeName) {
            //TODO: add back
            //sink.addInlineElement(calcOffsetForAttribute(annotation, attributeName, shiftOffset), false, text, false);
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

    private int calcOffsetForAttribute(@NotNull PsiAnnotation annotation, @NotNull String attributeName, int shiftOffset) {
        var attributeValue = annotation.findAttributeValue(attributeName);
        var result = 0;
        if (null != attributeValue) {
            result = attributeValue.getTextOffset() + attributeValue.getTextLength() - shiftOffset;
        }
        return result;
    }

    protected final void processImplicitsList(@NotNull PsiMember member, @NotNull Iterable<? extends CEImplicit> implicits, @NotNull InlayTreeSink sink) {
        for (var implicit : implicits) {
            var hasImplicitAnnotation = false;
            var annotation = member.getAnnotation(implicit.getBaseName());
            if (null != annotation) {
                for (var attribute : annotation.getAttributes()) {
                    var attributeName = attribute.getAttributeName();
                    var attributeInset = implicit.updateAttributesFor(member, annotation, attributeName);
                    addImplicitInlayForAttributeValue(annotation, attributeName, attributeInset, sink);
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

    private void addImplicitInlayForAttributeValue(PsiAnnotation annotation, @Nullable String attributeName,
                                                   @Nullable CEImplicitAttributeInsetValue attributeValueInset, InlayTreeSink sink) {
        if (null != attributeValueInset && null != attributeValueInset.getValue()) {
            var inlay = buildInlayWithText(attributeValueInset.getValue().toString(), "text." + getKey() + ".attributes.tooltip", null);
            addInlayInAttribute(annotation, attributeName, sink, inlay, attributeValueInset.getShiftOffset());
        }
    }

    private void addImplicitInlayForAnnotation(PsiAnnotation annotation, @Nullable String newAttributesList, @NotNull InlayTreeSink sink) {
        if (null != newAttributesList) {
            var inlay = buildInlayWithText(newAttributesList, "inlay." + getKey() + ".attributes.tooltip", null);
            addInlayInAnnotation(annotation, sink, inlay);
        }
    }

    private void addImplicitInlay(PsiElement element, @Nullable String fullText, @NotNull InlayTreeSink sink) {
        if (null != fullText) {
            var symbol = CESymbol.of(codePoint, fullText);
            var inlay = buildInlayWithEmoji(symbol, "inlay." + getKey() + ".annotations.tooltip", null);
            addInlayBlock(element, sink, inlay);
        }
    }

    @NotNull
    protected List<String> getDeactivatedCases() {
        return new ArrayList<>();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isDeactivatedFor(@NotNull JvmAnnotatedElement element) {
        return Arrays.stream(element.getAnnotations())
                .map(JvmAnnotation::getQualifiedName)
                .filter(Objects::nonNull)
                .anyMatch(annotationName -> getDeactivatedCases().stream().anyMatch(annotationName::equalsIgnoreCase));
    }

    protected abstract String getBaseName();

    protected abstract void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayTreeSink sink);
}