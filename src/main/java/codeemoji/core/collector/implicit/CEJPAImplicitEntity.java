package codeemoji.core.collector.implicit;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class CEJPAImplicitEntity implements CEIJPAImplicit {

    private final List<String> baseNames;

    public CEJPAImplicitEntity() {
        this.baseNames = CEJPAPersistenceUtils.buildBaseNames("Entity");
    }

    @Override
    public @Nullable String createAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation) {
        var nameAttr = new CEJPAAttribute("name", member.getName(), true);
        return formatAttributes(annotation, nameAttr);
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
