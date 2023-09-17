package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import codeemoji.core.util.CEUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class CESpringImplicitBean implements CEImplicitInterface {

    private final @NotNull String baseName;

    public CESpringImplicitBean() {
        this.baseName = "org.springframework.context.annotation.Bean";
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull final PsiMember member, @NotNull final PsiAnnotation memberAnnotation) {
        final var clazz = member.getContainingClass();
        Object attributeName = null;
        if (null != clazz) {
            final var classAnnotation = clazz.getAnnotation("org.springframework.context.annotation.Configuration");
            attributeName = CEUtils.uncapitalizeAsProperty(clazz.getName()) + "#" + member.getName();
            if (null != classAnnotation) {
                final var valueClassAttr = classAnnotation.findDeclaredAttributeValue("value");
                if (null != valueClassAttr) {
                    final var valueClassAttrText = valueClassAttr.getText().replaceAll("\"", "").trim();
                    if (!valueClassAttrText.isEmpty()) {
                        attributeName = valueClassAttrText + "#" + member.getName();
                    }
                }
            }
        }
        final var nameAttr = new CEImplicitAttribute("name", attributeName, true);
        return this.formatAttributes(memberAnnotation, nameAttr);
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull final PsiMember member) {
        return null;
    }
}
