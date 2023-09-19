package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicit;
import codeemoji.core.util.CEUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class CESpringImplicitBean implements CEImplicit {

    private final @NotNull String baseName;

    public CESpringImplicitBean() {
        baseName = "org.springframework.context.annotation.Bean";
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation memberAnnotation) {
        var clazz = member.getContainingClass();
        Object attributeName = null;
        if (null != clazz) {
            var classAnnotation = clazz.getAnnotation("org.springframework.context.annotation.Configuration");
            attributeName = CEUtils.uncapitalizeAsProperty(clazz.getName()) + "#" + member.getName();
            if (null != classAnnotation) {
                var valueClassAttr = classAnnotation.findDeclaredAttributeValue("value");
                if (null != valueClassAttr) {
                    var valueClassAttrText = valueClassAttr.getText().replaceAll("\"", "").trim();
                    if (!valueClassAttrText.isEmpty()) {
                        attributeName = valueClassAttrText + "#" + member.getName();
                    }
                }
            }
        }
        var nameAttr = new CEImplicitAttribute("name", attributeName, true);
        return formatAttributes(memberAnnotation, nameAttr);
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
