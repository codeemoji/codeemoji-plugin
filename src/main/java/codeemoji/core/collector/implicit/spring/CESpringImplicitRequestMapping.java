package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitAttributeInsetValue;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import codeemoji.core.util.CEUtils;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CESpringImplicitRequestMapping implements CEImplicitInterface {

    private final @NotNull String baseName;

    public CESpringImplicitRequestMapping() {
        baseName = "org.springframework.web.bind.annotation.RequestMapping";
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName) {
        var clazz = member.getContainingClass();
        Object nameValue = CEUtils.uncapitalizeAsProperty(member.getName());
        if (clazz != null) {
            var classAnnotation = clazz.getAnnotation(baseName);
            if (classAnnotation != null) {
                var nameClassAttr = classAnnotation.findDeclaredAttributeValue("name");
                if (nameClassAttr != null) {
                    var nameClassAttrText = nameClassAttr.getText().replaceAll("\"", "").trim();
                    if (!nameClassAttrText.isEmpty()) {
                        nameValue = nameClassAttrText + "#" + member.getName();
                    }
                }
            }
        }
        var nameAttr = new CEImplicitAttribute("name", nameValue, true);

        List<CEImplicitAttribute> attrList = new ArrayList<>();
        attrList.add(nameAttr);
        if (null == annotationFromBaseName.findAttribute("method")) {
            prepareAttribute(member, annotationFromBaseName, "method", attrList);
        }
        if (null == annotationFromBaseName.findAttribute("params")) {
            prepareAttribute(member, annotationFromBaseName, "params", attrList);
        }
        if (null == annotationFromBaseName.findAttribute("headers")) {
            prepareAttribute(member, annotationFromBaseName, "headers", attrList);
        }
        return formatAttributes(annotationFromBaseName, attrList.toArray(new CEImplicitAttribute[0]));
    }

    private void prepareAttribute(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName,
                                  @NotNull String attributeName, @NotNull List<CEImplicitAttribute> attrList) {
        var processAttribute = processAttribute(member, annotationFromBaseName, attributeName);
        var attribute = new CEImplicitAttribute(attributeName, processAttribute, false);
        attrList.add(attribute);
    }

    private @Nullable String processAttribute(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName,
                                              @NotNull String attributeName) {
        String result = null;
        var memberAttrValue = annotationFromBaseName.findDeclaredAttributeValue(attributeName);
        if (member instanceof PsiMethod method) {
            var clazz = method.getContainingClass();
            if (null != clazz) {
                var classAnnotation = clazz.getAnnotation(baseName);
                if (null != classAnnotation) {
                    var classAttrValue = classAnnotation.findDeclaredAttributeValue(attributeName);
                    if (null != classAttrValue) {
                        result = joinAttributeListValue(classAttrValue, memberAttrValue);
                    }
                }
            }
        }
        return result;
    }

    private @Nullable String joinAttributeListValue(@NotNull PsiAnnotationMemberValue classAttrValue,
                                                    @Nullable PsiAnnotationMemberValue memberAttrValue) {
        var textClass = classAttrValue.getText();
        var textMember = memberAttrValue != null ? memberAttrValue.getText() : "";
        var textClassCompare = textClass.replaceAll("\\{", "").replaceAll("\\}", "").replaceAll(" ", "");
        var textMemberCompare = textMember.replaceAll("\\{", "").replaceAll("\\}", "").replaceAll(" ", "");
        var classMethods = textClassCompare.split(",");
        var memberMethods = textMemberCompare.split(",");
        var classSet = new HashSet<>(Arrays.asList(classMethods));
        var memberSet = new HashSet<>(Arrays.asList(memberMethods));
        var mixedSet = new HashSet<>();
        mixedSet.addAll(classSet);
        mixedSet.addAll(memberSet);
        mixedSet.removeAll(memberSet);
        var setList = new ArrayList<>(mixedSet);
        Collections.reverse(setList);
        var mixedArray = setList.toArray();
        var mixedArrayValue = Arrays.toString(mixedArray).replaceAll("\\[", "").replaceAll("]", "");
        if (textMemberCompare.isEmpty()) {
            if (textClass.equals("{}")) {
                return null;
            } else if (textMember.contains("{")) {
                return mixedArrayValue;
            }
            return "{" + mixedArrayValue + "}";
        } else {
            if (mixedArrayValue.isEmpty()) {
                return null;
            }
            return ", " + mixedArrayValue;
        }
    }

    @Override
    public @Nullable String createAnnotationFor(@NotNull PsiMember member) {
        return null;
    }

    @Override
    public @Nullable CEImplicitAttributeInsetValue updateAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName, @NotNull String attributeName) {
        if (member instanceof PsiMethod method) {
            var attrValue = annotationFromBaseName.findDeclaredAttributeValue(attributeName);
            var attrValueUpdated = processAttribute(method, annotationFromBaseName, attributeName);
            if (null != attrValue && null != attrValueUpdated) {
                var shiftOffset = 1;
                if (!attrValue.getText().contains("{") || (attrValue.getText().contains("{") && !attrValue.getText().contains("}"))) {
                    shiftOffset = 0;
                    attrValueUpdated += "}";
                }
                return new CEImplicitAttributeInsetValue(attrValueUpdated, shiftOffset);
            }
        }
        return null;
    }
}
