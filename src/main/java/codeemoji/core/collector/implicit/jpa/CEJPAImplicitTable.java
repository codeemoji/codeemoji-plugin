package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitAttributeInsetValue;
import codeemoji.core.collector.implicit.CEImplicit;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CEJPAImplicitTable implements CEImplicit {

    private final @NotNull String nameSpace;
    private final @NotNull String baseName;

    public CEJPAImplicitTable(@NotNull String nameSpace) {
        this.nameSpace = nameSpace;
        this.baseName = nameSpace + ".Table";
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation memberAnnotation) {
        var attributeNameValue = member.getName();
        if (null != attributeNameValue) {
            attributeNameValue = attributeNameValue.toLowerCase();
        }
        var ann = member.getAnnotation(nameSpace + ".Entity");
        if (null != ann) {
            var valueAttribute = ann.findAttributeValue("name");
            if (null != valueAttribute && !"\"\"".equalsIgnoreCase(valueAttribute.getText())) {
                attributeNameValue = valueAttribute.getText().toLowerCase().replace("\"", "");
            }
        }
        var nameAttr = new CEImplicitAttribute("name", attributeNameValue, true);
        if (null == memberAnnotation.findAttribute("uniqueConstraints")) {
            var processUq = processUniqueConstraintsAttribute(member, memberAnnotation);
            var uqValue = null != processUq ? "{" + processUq + "}" : null;
            var uniqueConstraintsAttr = new CEImplicitAttribute("uniqueConstraints", uqValue, false);
            return formatAttributes(memberAnnotation, nameAttr, uniqueConstraintsAttr);
        } else {
            return formatAttributes(memberAnnotation, nameAttr);
        }
    }

    @Override
    public @Nullable CEImplicitAttributeInsetValue updateAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation memberAnnotation, @NotNull String attributeName) {
        if ("uniqueConstraints".equalsIgnoreCase(attributeName) && null != memberAnnotation.findDeclaredAttributeValue(attributeName)) {
            return new CEImplicitAttributeInsetValue(processUniqueConstraintsAttribute(member, memberAnnotation));
        }
        return null;
    }

    private @Nullable String processUniqueConstraintsAttribute(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName) {
        var result = new StringBuilder();
        var ucValue = annotationFromBaseName.findAttributeValue("uniqueConstraints");
        var ucValueCompare = null != ucValue ? ucValue.getText().replaceAll(" ", "") : "{}";
        if (member instanceof PsiClass clazz) {
            clazz.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull PsiField field) {
                    var columnAnnotation = field.getAnnotation(nameSpace + ".Column");
                    if (null != columnAnnotation) {
                        var uniqueValue = columnAnnotation.findAttributeValue("unique");
                        if (null != uniqueValue && "true".equalsIgnoreCase(uniqueValue.getText())) {
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
            if (!result.isEmpty() && !"{}".equalsIgnoreCase(ucValueCompare)) {
                result.insert(0, ", ");
            }
        }
        return !result.isEmpty() ? result.toString() : null;
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull PsiMember member) {
        return null;
    }
}
