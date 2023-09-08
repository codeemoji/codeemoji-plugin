package codeemoji.core.collector.implicit;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@Getter
@SuppressWarnings("UnstableApiUsage")
public non-sealed class CEJPAEntityCollector extends CEImplicitCollector {

    public final String baseName;
    public final String keyId;
    public final Integer codePoint;

    public CEJPAEntityCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable Integer codePoint) {
        super(editor);
        this.keyId = keyId;
        this.codePoint = codePoint;
        this.baseName = "javax.persistence.Entity";
    }

    @Override
    public boolean hasImplicitBase(@Nullable PsiClass containingClass) {
        if (containingClass != null) {
            var annotations = containingClass.getAnnotations();
            for (PsiAnnotation annotation : annotations) {
                if (baseName.equalsIgnoreCase(annotation.getQualifiedName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink) {
        if (member instanceof PsiField field) {

            var implicits = new ArrayList<CEIJPAImplicit>();
            implicits.add(new CEJPAColumnImplicit("javax.persistence.Column"));
            implicits.add(new CEJPABasicImplicit("javax.persistence.Basic"));

            for (var implicit : implicits) {
                var hasImplicitAnnotation = false;
                for (var annotation : field.getAnnotations()) {
                    if (annotation.getQualifiedName() != null) {
                        var qualifiedName = annotation.getQualifiedName();
                        if (qualifiedName.equalsIgnoreCase(implicit.name())) {
                            var complement = implicit.processAttributes(field, annotation.getAttributes());
                            addImplicitInlayForAttributes(field, annotation, complement, sink);
                            hasImplicitAnnotation = true;
                            break;
                        }
                    }
                }
                if (!hasImplicitAnnotation) {
                    addImplicitInlay(field, implicit.buildAnnotationFor(field), sink);
                }
            }
        }
    }

    private void addImplicitInlayForAttributes(PsiField field, PsiAnnotation annotation, @Nullable String complement, @NotNull InlayHintsSink sink) {
        if (complement != null) {
            var symbol = new CESymbol(getCodePoint(), complement);
            var inlay = buildInlay(symbol, "inlay." + getKeyId() + ".tooltip", null);
            addInlayInline(annotation, sink, inlay);
        }
    }

    private void addImplicitInlay(PsiField field, @Nullable String fullText, @NotNull InlayHintsSink sink) {
        if (fullText != null) {
            var symbol = new CESymbol(getCodePoint(), fullText);
            var inlay = buildInlay(symbol, "inlay." + getKeyId() + ".tooltip", null);
            addInlayBlock(field, sink, inlay);
        }
    }


}
