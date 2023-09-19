package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicit;
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
public class CEJPAImplicitBasic implements CEImplicit {

    private final @NotNull String baseName;
    private final @NotNull String nameSpace;
    private final List<String> deactivatedCases = new ArrayList<>();
    private final List<String> deactivatedInTypeCases = new ArrayList<>();

    public CEJPAImplicitBasic(@NotNull String nameSpace) {
        this.nameSpace = nameSpace;
        baseName = nameSpace + ".Basic";
        deactivatedCases.add(nameSpace + ".Transient");
        deactivatedInTypeCases.add(nameSpace + ".Embeddable");
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation memberAnnotation) {
        if (null != member.getAnnotation(nameSpace + ".Id")) {
            var optionalAttr = new CEImplicitAttribute("optional", "false", false);
            return formatAttributes(memberAnnotation, optionalAttr);
        }
        return null;
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull PsiMember member) {
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