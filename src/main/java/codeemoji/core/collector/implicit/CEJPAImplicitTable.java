package codeemoji.core.collector.implicit;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class CEJPAImplicitTable implements CEIJPAImplicit {

    private final List<String> baseNames;

    public CEJPAImplicitTable() {
        this.baseNames = CEJPAPersistenceUtils.buildBaseNames("Table");
    }

    @Override
    public @Nullable String processAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation) {
        var attributeNameValue = member.getName();
        if (attributeNameValue != null) {
            attributeNameValue = attributeNameValue.toLowerCase();
        }
        var ann = CEJPAPersistenceUtils.searchAnnotation(member, "Entity");
        if (ann != null) {
            var valueAttribute = ann.findAttributeValue("name");
            if (valueAttribute != null && !valueAttribute.getText().equalsIgnoreCase("\"\"")) {
                attributeNameValue = valueAttribute.getText().toLowerCase().replace("\"", "");
            }
        }
        var nameAttr = new CEJPAAttribute("name", attributeNameValue, true);
        return buildAttributes(annotation, nameAttr);
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
