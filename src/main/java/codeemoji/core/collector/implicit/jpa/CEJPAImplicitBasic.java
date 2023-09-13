package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitInterface;
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
public class CEJPAImplicitBasic implements CEImplicitInterface {

    private final @NotNull List<String> baseNames;
    private final List<String> deactivatedCases = new ArrayList<>();
    private final List<String> deactivatedInTypeCases = new ArrayList<>();

    public CEJPAImplicitBasic() {
        baseNames = CEJPAUtils.buildBaseNames("Basic");
        this.deactivatedCases.addAll(CEJPAUtils.buildBaseListFor("Transient"));
        this.deactivatedInTypeCases.addAll(CEJPAUtils.buildBaseListFor("Embeddable"));
    }

    @Override
    public @Nullable String createAttributes(@NotNull final PsiMember member, @NotNull final PsiAnnotation annotation) {
        if (null != CEJPAUtils.searchAnnotation(member, "Id")) {
            final var optionalAttr = new CEImplicitAttribute("optional", "false", false);
            return this.formatAttributes(annotation, optionalAttr);
        }
        return null;
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull final PsiMember member) {
        if (member instanceof final PsiField field) {
            final var type = field.getType();
            if ((
                    CEUtils.isPrimitiveOrWrapperType(type) ||
                            CEUtils.isDateDBType(type) ||
                            CEUtils.isEnumType(type) ||
                            CEUtils.isSerializableType(type)
            ) &&
                    !CEUtils.isConstant(field) &&
                    !this.isDeactivatedFor(field) &&
                    !this.isDeactivatedInType(field.getType())) {
                return "@Basic";
            }
        }
        return null;
    }
}