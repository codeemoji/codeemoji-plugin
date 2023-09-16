package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class CEJPAImplicitEntity implements CEImplicitInterface {

    private final @NotNull String nameSpace;
    private final @NotNull String baseName;

    public CEJPAImplicitEntity(@NotNull String nameSpace) {
        this.nameSpace = nameSpace;
        baseName = nameSpace + ".Entity";
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
