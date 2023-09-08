package codeemoji.core.collector.implicit;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.psi.PsiModifier.FINAL;
import static com.intellij.psi.PsiModifier.STATIC;

public record CEJPAColumnImplicit(String name) implements CEIJPAImplicit {
    @Override
    public @Nullable String processAttributes(@NotNull PsiMember member, @NotNull List<JvmAnnotationAttribute> attributes) {
        String result = null;
        var hasName = false;
        for (var attr : attributes) {
            if (attr.getAttributeName().equalsIgnoreCase("name")) {
                hasName = true;
            }
        }
        if (!hasName) {
            result = "name = " + member.getName();
        }
        return result;
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull PsiMember member) {
        if (member instanceof PsiField field) {
            var modifierList = field.getModifierList();
            if (modifierList != null &&
                    !modifierList.hasExplicitModifier(STATIC) &&
                    !modifierList.hasExplicitModifier(FINAL) &&
                    !isDeactivatedFor(field.getAnnotations())
            ) {
                var name = field.getName();
                return "@Column(name = \"" + name + "\")";
            }
        }
        return null;
    }

    private boolean isDeactivatedFor(PsiAnnotation @NotNull [] otherAnnotations) {
        for (var annotation : otherAnnotations) {
            var annotationName = annotation.getQualifiedName();
            if (annotationName != null) {
                if (annotationName.equalsIgnoreCase("javax.persistence.Transient") ||
                        annotationName.equalsIgnoreCase("javax.persistence.JoinColumn") ||
                        annotationName.equalsIgnoreCase("javax.persistence.OneToMany")) {
                    return true;
                }
            }
        }
        return false;
    }
}
