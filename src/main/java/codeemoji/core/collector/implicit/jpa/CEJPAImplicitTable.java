package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitAttributeInsetValue;
import codeemoji.core.collector.implicit.CEImplicitInterface;
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
public class CEJPAImplicitTable implements CEImplicitInterface {

    private final @NotNull String nameSpace;
    private final @NotNull String baseName;

    public CEJPAImplicitTable(@NotNull String nameSpace) {
        this.nameSpace = nameSpace;
        baseName = nameSpace + ".Table";
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName) {
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
        if (null == annotationFromBaseName.findAttribute("uniqueConstraints")) {
            var processUq = processUniqueConstraintsAttribute(member, annotationFromBaseName);
            var uqValue = null != processUq ? "{" + processUq + "}" : null;
            var uniqueConstraintsAttr = new CEImplicitAttribute("uniqueConstraints", uqValue, false);
            return formatAttributes(annotationFromBaseName, nameAttr, uniqueConstraintsAttr);
        } else {
            return formatAttributes(annotationFromBaseName, nameAttr);
        }
    }

    @Override
    public @Nullable CEImplicitAttributeInsetValue updateAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName, @NotNull String attributeName) {
        if (attributeName.equalsIgnoreCase("uniqueConstraints") && null != annotationFromBaseName.findDeclaredAttributeValue(attributeName)) {
            return new CEImplicitAttributeInsetValue(processUniqueConstraintsAttribute(member, annotationFromBaseName));
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
