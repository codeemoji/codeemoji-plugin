package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class CEJPAImplicitEntity implements CEImplicitInterface {

    private final @NotNull List<String> baseNames;

    public CEJPAImplicitEntity() {
        this.baseNames = CEJPAUtils.buildBaseNames("Entity");
    }

    @Override
    public @Nullable String createAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation) {
        var nameAttr = new CEImplicitAttribute("name", member.getName(), true);
        return formatAttributes(annotation, nameAttr);
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
