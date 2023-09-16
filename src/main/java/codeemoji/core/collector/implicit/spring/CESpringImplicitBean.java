package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitInterface;
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
        var nameAttr = new CEImplicitAttribute("name", member.getName(), true);
        return formatAttributes(annotationFromBaseName, nameAttr);
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
