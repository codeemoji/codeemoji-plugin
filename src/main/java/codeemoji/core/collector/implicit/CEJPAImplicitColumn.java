package codeemoji.core.collector.implicit;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.PsiModifier.FINAL;
import static com.intellij.psi.PsiModifier.STATIC;

@Getter
public class CEJPAImplicitColumn implements CEIJPAImplicit {

    private final List<String> baseNames;
    private final List<String> deactivatedCases = new ArrayList<>();
    private final List<String> deactivatedInTypeCases = new ArrayList<>();

    public CEJPAImplicitColumn() {
        this.baseNames = CEJPAPersistenceUtils.buildBaseNames("Column");
        deactivatedCases.addAll(CEJPAPersistenceUtils.buildBaseListFor("Transient", "JoinColumn", "OneToMany", "AttributeOverride"));
        deactivatedInTypeCases.addAll(CEJPAPersistenceUtils.buildBaseListFor("Embeddable"));
    }

    @Override
    public @Nullable String createAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation) {
        var nameAttr = new CEJPAAttribute("name", member.getName(), true);
        var ucValue = annotation.findAttributeValue("unique");
        if (ucValue != null && ucValue.getText().equalsIgnoreCase("true")) {
            var nullableAttr = new CEJPAAttribute("nullable", "false", false);
            return formatAttributes(annotation, nameAttr, nullableAttr);
        }
        return formatAttributes(annotation, nameAttr);
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull PsiMember member) {
        if (member instanceof PsiField field) {
            var modifierList = field.getModifierList();
            if (modifierList != null &&
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
