package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitCollector;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract class CEJPACollector extends CEImplicitCollector {

    public final @NotNull String keyId;
    public final int codePoint;

    CEJPACollector(@NotNull final Editor editor, @NotNull final String keyId, final int codePoint) {
        super(editor);
        this.keyId = keyId;
        this.codePoint = codePoint;
    }

    void processImplicits(@NotNull final PsiMember member, @NotNull final Iterable<? extends CEImplicitInterface> implicits, @NotNull final InlayHintsSink sink) {
        for (final var implicit : implicits) {
            var hasImplicitAnnotation = false;
            for (final var name : implicit.getBaseNames()) {
                final var annotation = member.getAnnotation(name);
                if (null != annotation) {
                    for (final var attribute : annotation.getAttributes()) {
                        final var attributeName = attribute.getAttributeName();
                        final var valueComplement = implicit.updateAttributes(member, annotation, attributeName);
                        this.addImplicitInlayForAttributeValue(annotation, attributeName, valueComplement, sink);
                    }
                    final var newAttributesList = implicit.createAttributes(member, annotation);
                    this.addImplicitInlayForAnnotation(annotation, newAttributesList, sink);
                    hasImplicitAnnotation = true;
                    break;
                }
            }
            if (!hasImplicitAnnotation) {
                final var complement = implicit.buildAnnotationFor(member);
                if (null != complement) {
                    this.addImplicitInlay(member, complement, sink);
                }
            }
        }
    }

    private void addImplicitInlayForAttributeValue(final PsiAnnotation annotation, @Nullable final String attributeName, @Nullable final String attributeValue, final InlayHintsSink sink) {
        if (null != attributeValue) {
            final var inlay = this.buildInlayWithText(attributeValue, "inlay." + keyId + ".attributes.tooltip", null);
            this.addInlayInAttribute(annotation, attributeName, sink, inlay);
        }
    }

    private void addImplicitInlayForAnnotation(final PsiAnnotation annotation, @Nullable final String newAttributesList, @NotNull final InlayHintsSink sink) {
        if (null != newAttributesList) {
            final var inlay = this.buildInlayWithText(newAttributesList, "inlay." + keyId + ".attributes.tooltip", null);
            this.addInlayInAnnotation(annotation, sink, inlay);
        }
    }

    private void addImplicitInlay(final PsiElement element, @Nullable final String fullText, @NotNull final InlayHintsSink sink) {
        if (null != fullText) {
            final var symbol = new CESymbol(codePoint, fullText);
            final var inlay = this.buildInlayWithEmoji(symbol, "inlay." + keyId + ".annotations.tooltip", null);
            this.addInlayBlock(element, sink, inlay);
        }
    }

}
