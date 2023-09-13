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
import java.util.List;

import static com.intellij.psi.PsiModifier.FINAL;
import static com.intellij.psi.PsiModifier.STATIC;

@Getter
public class CEJPAImplicitColumn implements CEImplicitInterface {

    private final @NotNull List<String> baseNames;
    private final List<String> deactivatedCases = new ArrayList<>();
    private final List<String> deactivatedInTypeCases = new ArrayList<>();

    public CEJPAImplicitColumn() {
        baseNames = CEJPAUtils.buildBaseNames("Column");
        this.deactivatedCases.addAll(CEJPAUtils.buildBaseListFor("Transient", "JoinColumn", "OneToMany", "AttributeOverride"));
        this.deactivatedInTypeCases.addAll(CEJPAUtils.buildBaseListFor("Embeddable"));
    }

    @Override
    public @Nullable String createAttributes(@NotNull final PsiMember member, @NotNull final PsiAnnotation annotation) {
        final var nameAttr = new CEImplicitAttribute("name", member.getName(), true);
        final var ucValue = annotation.findAttributeValue("unique");
        if (null != ucValue && "true".equalsIgnoreCase(ucValue.getText())) {
            final var nullableAttr = new CEImplicitAttribute("nullable", "false", false);
            return this.formatAttributes(annotation, nameAttr, nullableAttr);
        }
        return this.formatAttributes(annotation, nameAttr);
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull final PsiMember member) {
        if (member instanceof final PsiField field) {
            final var modifierList = field.getModifierList();
            if (null != modifierList &&
                    !modifierList.hasExplicitModifier(STATIC) &&
                    !modifierList.hasExplicitModifier(FINAL) &&
                    !this.isDeactivatedFor(field) &&
                    !this.isDeactivatedInType(field.getType())
            ) {
                final var name = field.getName();
                return "@Column(name = \"" + name + "\")";
            }
        }
        return null;
    }
}
