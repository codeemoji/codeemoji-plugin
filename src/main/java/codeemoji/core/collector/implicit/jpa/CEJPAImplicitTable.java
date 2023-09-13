package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CEJPAImplicitTable implements CEImplicitInterface {

    private final @NotNull List<String> baseNames;

    public CEJPAImplicitTable() {
        baseNames = CEJPAUtils.buildBaseNames("Table");
    }

    @Override
    public @Nullable String createAttributes(@NotNull final PsiMember member, @NotNull final PsiAnnotation annotation) {
        var attributeNameValue = member.getName();
        if (null != attributeNameValue) {
            attributeNameValue = attributeNameValue.toLowerCase();
        }
        final var ann = CEJPAUtils.searchAnnotation(member, "Entity");
        if (null != ann) {
            final var valueAttribute = ann.findAttributeValue("name");
            if (null != valueAttribute && !"\"\"".equalsIgnoreCase(valueAttribute.getText())) {
                attributeNameValue = valueAttribute.getText().toLowerCase().replace("\"", "");
            }
        }
        final var nameAttr = new CEImplicitAttribute("name", attributeNameValue, true);
        if (null == annotation.findAttribute("uniqueConstraints")) {
            final var processUq = this.processUniqueConstraints(member, annotation);
            final var uqValue = null != processUq ? "{" + processUq + "}" : null;
            final var uniqueConstraintsAttr = new CEImplicitAttribute("uniqueConstraints", uqValue, false);
            return this.formatAttributes(annotation, nameAttr, uniqueConstraintsAttr);
        } else {
            return this.formatAttributes(annotation, nameAttr);
        }
    }

    @Override
    public @Nullable String updateAttributes(@NotNull final PsiMember member, @NotNull final PsiAnnotation annotation, @NotNull final String attributeName) {
        final var UNIQUE_CONSTRAINTS_NAME = "uniqueConstraints";
        if (attributeName.equalsIgnoreCase(UNIQUE_CONSTRAINTS_NAME) && null != annotation.findAttribute(UNIQUE_CONSTRAINTS_NAME)) {
            return this.processUniqueConstraints(member, annotation);
        }
        return null;
    }

    private @Nullable String processUniqueConstraints(@NotNull final PsiMember member, @NotNull final PsiAnnotation annotation) {
        final var result = new StringBuilder();
        final var ucValue = annotation.findAttributeValue("uniqueConstraints");
        final var ucValueCompare = null != ucValue ? ucValue.getText().replaceAll(" ", "") : "{}";
        if (member instanceof final PsiClass clazz) {
            clazz.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull final PsiField field) {
                    final var columnAnnotation = CEJPAUtils.searchAnnotation(field, "Column");
                    if (null != columnAnnotation) {
                        final var uniqueValue = columnAnnotation.findAttributeValue("unique");
                        if (null != uniqueValue && "true".equalsIgnoreCase(uniqueValue.getText())) {
                            final var addValue = "@UniqueConstraint(columnNames = {\"" + field.getName() + "\"})";
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
            if (!result.isEmpty() && !"{}".equalsIgnoreCase(ucValueCompare)) {
                result.insert(0, ", ");
            }
        }
        return !result.isEmpty() ? result.toString() : null;
    }

    @Override
    public @Nullable String buildAnnotationFor(@NotNull final PsiMember member) {
        return null;
    }
}
