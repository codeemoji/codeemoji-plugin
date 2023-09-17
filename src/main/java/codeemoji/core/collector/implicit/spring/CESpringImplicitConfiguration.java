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
public class CESpringImplicitConfiguration implements CEImplicitInterface {

    private final @NotNull String baseName;

    public CESpringImplicitConfiguration() {
        baseName = "org.springframework.context.annotation.Configuration";
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation memberAnnotation) {
        var value = CEUtils.uncapitalizeAsProperty(member.getName());
        var nameAttr = new CEImplicitAttribute("value", value, true);
        return formatAttributes(memberAnnotation, nameAttr);
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
