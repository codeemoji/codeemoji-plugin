package codeemoji.core.collector.implicit;

import codeemoji.core.util.CEUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CEJPAImplicitBasic implements CEIJPAImplicit {

    private final List<String> baseNames;
    private final List<String> deactivatedCases = new ArrayList<>();
    private final List<String> deactivatedInTypeCases = new ArrayList<>();

    public CEJPAImplicitBasic() {
        this.baseNames = CEJPAPersistenceUtils.buildBaseNames("Basic");
        deactivatedCases.addAll(CEJPAPersistenceUtils.buildBaseListFor("Transient"));
        deactivatedInTypeCases.addAll(CEJPAPersistenceUtils.buildBaseListFor("Embeddable"));
    }

    @Override
    public @Nullable String createAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation) {
        if (CEJPAPersistenceUtils.searchAnnotation(member, "Id") != null) {
            var optionalAttr = new CEJPAAttribute("optional", "false", false);
            return formatAttributes(annotation, optionalAttr);
        }
        return null;
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull PsiMember member) {
        if (member instanceof PsiField field) {
            var type = field.getType();
            if ((
                    CEUtils.isPrimitiveOrWrapperType(type) ||
                            CEUtils.isDateDBType(type) ||
                            CEUtils.isEnumType(type) ||
                            CEUtils.isSerializableType(type)
            ) &&
                    !CEUtils.isConstant(field) &&
                    !isDeactivatedFor(field) &&
                    !isDeactivatedInType(field.getType())) {
                return "@Basic";
            }
        }
        return null;
    }
}