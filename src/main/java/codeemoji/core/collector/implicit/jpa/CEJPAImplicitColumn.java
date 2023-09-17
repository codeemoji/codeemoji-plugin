package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.intellij.psi.PsiModifier.FINAL;
import static com.intellij.psi.PsiModifier.STATIC;

@Getter
public class CEJPAImplicitColumn implements CEImplicitInterface {

    private final @NotNull String baseName;
    private final @NotNull String nameSpace;
    private final List<String> deactivatedCases = new ArrayList<>();
    private final List<String> deactivatedInTypeCases = new ArrayList<>();

    public CEJPAImplicitColumn(@NotNull String nameSpace) {
        this.nameSpace = nameSpace;
        baseName = nameSpace + ".Column";
        deactivatedCases.addAll(Arrays.asList(nameSpace + ".Transient",
                nameSpace + ".JoinColumn",
                nameSpace + ".OneToMany",
                nameSpace + ".AttributeOverride"));
        deactivatedInTypeCases.add(nameSpace + ".Embeddable");
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation memberAnnotation) {
        var nameAttr = new CEImplicitAttribute("name", member.getName(), true);
        var ucValue = memberAnnotation.findAttributeValue("unique");
        if (null != ucValue && "true".equalsIgnoreCase(ucValue.getText())) {
            var nullableAttr = new CEImplicitAttribute("nullable", "false", false);
            return formatAttributes(memberAnnotation, nameAttr, nullableAttr);
        }
        return formatAttributes(memberAnnotation, nameAttr);
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull PsiMember member) {
        if (member instanceof PsiField field) {
            var modifierList = field.getModifierList();
            if (null != modifierList &&
                    !modifierList.hasExplicitModifier(STATIC) &&
                    !modifierList.hasExplicitModifier(FINAL) &&
                    !isDeactivatedFor(field) &&
                    !isDeactivatedInType(field.getType())
            ) {
                var name = field.getName();
                return "@Column(name = \"" + name + "\")";
            }
        }
        return null;
    }
}
