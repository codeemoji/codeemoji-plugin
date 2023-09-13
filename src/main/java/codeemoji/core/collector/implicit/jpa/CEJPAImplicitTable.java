package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CEJPAImplicitTable implements CEImplicitInterface {

    private final List<String> baseNames;

    public CEJPAImplicitTable() {
        this.baseNames = CEJPAUtils.buildBaseNames("Table");
    }

    @Override
    public @Nullable String createAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation) {
        var attributeNameValue = member.getName();
        if (attributeNameValue != null) {
            attributeNameValue = attributeNameValue.toLowerCase();
        }
        var ann = CEJPAUtils.searchAnnotation(member, "Entity");
        if (ann != null) {
            var valueAttribute = ann.findAttributeValue("name");
            if (valueAttribute != null && !valueAttribute.getText().equalsIgnoreCase("\"\"")) {
                attributeNameValue = valueAttribute.getText().toLowerCase().replace("\"", "");
            }
        }
        var nameAttr = new CEImplicitAttribute("name", attributeNameValue, true);
        if (annotation.findAttribute("uniqueConstraints") == null) {
            var processUq = processUniqueConstraints(member, annotation);
            var uqValue = processUq != null ? "{" + processUq + "}" : null;
            var uniqueConstraintsAttr = new CEImplicitAttribute("uniqueConstraints", uqValue, false);
            return formatAttributes(annotation, nameAttr, uniqueConstraintsAttr);
        } else {
            return formatAttributes(annotation, nameAttr);
        }
    }

    @Override
    public @Nullable String updateAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation, @NotNull String attributeName) {
        final String UNIQUE_CONSTRAINTS_NAME = "uniqueConstraints";
        if (attributeName.equalsIgnoreCase(UNIQUE_CONSTRAINTS_NAME) && annotation.findAttribute(UNIQUE_CONSTRAINTS_NAME) != null) {
            return processUniqueConstraints(member, annotation);
        }
        return null;
    }

    private @Nullable String processUniqueConstraints(@NotNull PsiMember member, @NotNull PsiAnnotation annotation) {
        StringBuilder result = new StringBuilder();
        var ucValue = annotation.findAttributeValue("uniqueConstraints");
        var ucValueCompare = ucValue != null ? ucValue.getText().replaceAll(" ", "") : "{}";
        if (member instanceof PsiClass clazz) {
            clazz.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull PsiField field) {
                    var columnAnnotation = CEJPAUtils.searchAnnotation(field, "Column");
                    if (columnAnnotation != null) {
                        var uniqueValue = columnAnnotation.findAttributeValue("unique");
                        if (uniqueValue != null && uniqueValue.getText().equalsIgnoreCase("true")) {
                            var addValue = "@UniqueConstraint(columnNames = {\"" + field.getName() + "\"})";
                            if (!ucValueCompare.contains(addValue.replace(" ", ""))) {
                                if (!result.isEmpty()) {
                                    result.append(", ");
                                }
                                result.append(addValue);
                            }
                        }
                    }
                    super.visitField(field);
                }
            });
            if (!result.isEmpty() && !ucValueCompare.equalsIgnoreCase("{}")) {
                result.insert(0, ", ");
            }
        }
        return !result.isEmpty() ? result.toString() : null;
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
