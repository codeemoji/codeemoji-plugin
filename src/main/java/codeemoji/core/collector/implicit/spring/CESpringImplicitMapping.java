package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import codeemoji.core.collector.implicit.CEImplicitAttributeInsetValue;
import codeemoji.core.collector.implicit.CEImplicitInterface;
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
public abstract class CESpringImplicitMapping implements CEImplicitInterface {

    @Override
    public final @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation memberAnnotation) {
        List<CEImplicitAttribute> attrList = new ArrayList<>(createChildrenAttributesFor(member, memberAnnotation));
        attrList.add(processNameAttribute(member));
        if (null == memberAnnotation.findAttribute("params")) {
            prepareAttribute(member, memberAnnotation, "params", attrList);
        }
        if (null == memberAnnotation.findAttribute("headers")) {
            prepareAttribute(member, memberAnnotation, "headers", attrList);
        }
        return formatAttributes(memberAnnotation, attrList.toArray(new CEImplicitAttribute[0]));
    }

    public @NotNull List<CEImplicitAttribute> createChildrenAttributesFor(@NotNull PsiMember member,
                                                                          @NotNull PsiAnnotation memberAnnotation) {
        return new ArrayList<>();
    }

    @NotNull
    public CEImplicitAttribute processNameAttribute(@NotNull PsiMember member) {
        var clazz = member.getContainingClass();
        Object nameValue = member.getName();
        if (clazz != null) {
            var classAnnotation = clazz.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
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
        return new CEImplicitAttribute("name", nameValue, true);
    }

    public void prepareAttribute(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName,
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
                var classAnnotation = clazz.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
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
    public @Nullable CEImplicitAttributeInsetValue updateAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation memberAnnotation, @NotNull String attributeName) {
        if (member instanceof PsiMethod method) {
            var attrValue = memberAnnotation.findDeclaredAttributeValue(attributeName);
            var attrValueUpdated = processAttribute(method, memberAnnotation, attributeName);
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