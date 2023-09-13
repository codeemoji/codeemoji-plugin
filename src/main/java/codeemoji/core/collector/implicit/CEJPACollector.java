package codeemoji.core.collector.implicit;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract sealed class CEJPACollector extends CEImplicitCollector
        permits CEJPAEmbeddableCollector, CEJPAEntityCollector {

    public final String keyId;
    public final Integer codePoint;

    public CEJPACollector(@NotNull Editor editor, @NotNull String keyId, @Nullable Integer codePoint) {
        super(editor);
        this.keyId = keyId;
        this.codePoint = codePoint;
    }

    protected void processImplicits(@NotNull PsiMember member, @NotNull List<CEIJPAImplicit> implicits, @NotNull InlayHintsSink sink) {
        for (var implicit : implicits) {
            var hasImplicitAnnotation = false;
            for (var name : implicit.getBaseNames()) {
                var annotation = member.getAnnotation(name);
                if (annotation != null) {
                    for (var attribute : annotation.getAttributes()) {
                        var attributeName = attribute.getAttributeName();
                        var valueComplement = implicit.updateAttributes(member, annotation, attributeName);
                        addImplicitInlayForAttributeValue(annotation, attributeName, valueComplement, sink);
                    }
                    var newAttributesList = implicit.createAttributes(member, annotation);
                    addImplicitInlayForAnnotation(annotation, newAttributesList, sink);
                    hasImplicitAnnotation = true;
                    break;
                }
            }
            if (!hasImplicitAnnotation) {
                var complement = implicit.buildAnnotationFor(member);
                if (complement != null) {
                    addImplicitInlay(member, complement, sink);
                }
            }
        }
    }

    private void addImplicitInlayForAttributeValue(PsiAnnotation annotation, @Nullable String attributeName, @Nullable String attributeValue, InlayHintsSink sink) {
        if (attributeValue != null) {
            var inlay = buildInlayWithText(attributeValue, "inlay." + getKeyId() + ".attributes.tooltip", null);
            addInlayInAttribute(annotation, attributeName, sink, inlay);
        }
    }

    private void addImplicitInlayForAnnotation(PsiAnnotation annotation, @Nullable String newAttributesList, @NotNull InlayHintsSink sink) {
        if (newAttributesList != null) {
            var inlay = buildInlayWithText(newAttributesList, "inlay." + getKeyId() + ".attributes.tooltip", null);
            addInlayInAnnotation(annotation, sink, inlay);
        }
    }


    private void addImplicitInlay(PsiElement element, @Nullable String fullText, @NotNull InlayHintsSink sink) {
        if (fullText != null) {
            var symbol = new CESymbol(getCodePoint(), fullText);
            var inlay = buildInlayWithEmoji(symbol, "inlay." + getKeyId() + ".annotations.tooltip", null);
            addInlayBlock(element, sink, inlay);
        }
    }


}
