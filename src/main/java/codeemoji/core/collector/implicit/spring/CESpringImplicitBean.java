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
        baseName = "org.springframework.context.annotation.Bean";
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName) {
        var clazz = member.getContainingClass();
        Object attributeName = null;
        if (clazz != null) {
            var classAnnotation = clazz.getAnnotation("org.springframework.context.annotation.Configuration");
            attributeName = CEUtils.uncapitalizeAsProperty(clazz.getName()) + "#" + member.getName();
            if (classAnnotation != null) {
                var valueClassAttr = classAnnotation.findDeclaredAttributeValue("value");
                if (valueClassAttr != null) {
                    var valueClassAttrText = valueClassAttr.getText().replaceAll("\"", "").trim();
                    if (!valueClassAttrText.isEmpty()) {
                        attributeName = valueClassAttrText + "#" + member.getName();
                    }
                }
            }
        }
        var nameAttr = new CEImplicitAttribute("name", attributeName, true);
        return formatAttributes(annotationFromBaseName, nameAttr);
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
