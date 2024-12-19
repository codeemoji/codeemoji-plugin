package codeemoji.core.collector.implicit;

import codeemoji.core.collector.CECollector;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
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
@SuppressWarnings("UnstableApiUsage")
public abstract class CEImplicitCollector extends CECollector {

    public final int codePoint;
    private final String keyId;

    protected CEImplicitCollector(@NotNull Editor editor, SettingsKey<?> settingsKey, int codePoint) {
        super(editor, settingsKey);
        this.codePoint = codePoint;
        this.keyId = settingsKey.getId(); //TODO:t his could be done better
    }

    @Override
    public final boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitClass(@NotNull PsiClass aClass) {
                    if (hasImplicitBase(aClass)) {
                        processImplicitsFor(aClass, inlayHintsSink);
                    }
                    super.visitClass(aClass);
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
                        return null != clazz.getAnnotation(getBaseName()) && !isDeactivatedFor(clazz);
                    }
                    return false;
                }
            });
        }
        return false;
    }

    protected final void addInlayInAnnotation(@Nullable PsiAnnotation annotation, @NotNull InlayHintsSink sink, @NotNull InlayPresentation inlay) {
        if (null != annotation) {
            sink.addInlineElement(calcOffsetForAnnotation(annotation), false, inlay, false);
        }
    }

    protected final void addInlayInAttribute(@Nullable PsiAnnotation annotation, @Nullable String attributeName,
                                             @NotNull InlayHintsSink sink, @NotNull InlayPresentation inlay, int shiftOffset) {
        if (null != annotation && null != attributeName) {
            sink.addInlineElement(calcOffsetForAttribute(annotation, attributeName, shiftOffset), false, inlay, false);
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

    protected final void processImplicitsList(@NotNull PsiMember member, @NotNull Iterable<? extends CEImplicit> implicits, @NotNull InlayHintsSink sink) {
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
                                                   @Nullable CEImplicitAttributeInsetValue attributeValueInset, InlayHintsSink sink) {
        if (null != attributeValueInset && null != attributeValueInset.getValue()) {
            var inlay = buildInlayWithText(attributeValueInset.getValue().toString(), "inlay." + keyId + ".attributes.tooltip", null);
            addInlayInAttribute(annotation, attributeName, sink, inlay, attributeValueInset.getShiftOffset());
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
            var symbol = CESymbol.of(codePoint, fullText);
            var inlay = buildInlayWithEmoji(symbol, "inlay." + keyId + ".annotations.tooltip", null);
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

    protected abstract void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink);
}