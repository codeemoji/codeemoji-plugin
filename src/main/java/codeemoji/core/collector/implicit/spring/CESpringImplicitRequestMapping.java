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

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CESpringImplicitRequestMapping implements CEImplicitInterface {

    private final @NotNull String baseName;

    public CESpringImplicitRequestMapping() {
        baseName = "org.springframework.web.bind.annotation.RequestMapping";
    }

    @Override
    public @Nullable String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName) {
        var nameValue = CEUtils.uncapitalizeAsProperty(member.getName());
        var nameAttr = new CEImplicitAttribute("name", nameValue, true);
        if (null == annotationFromBaseName.findAttribute("method")) {
            var processMethod = processMethodAttribute(member, annotationFromBaseName);
            var methodAttr = new CEImplicitAttribute("method", processMethod, false);
            return formatAttributes(annotationFromBaseName, nameAttr, methodAttr);
        } else {
            return formatAttributes(annotationFromBaseName, nameAttr);
        }
    }

    private @Nullable String processMethodAttribute(@NotNull PsiMember member, @NotNull PsiAnnotation annotationFromBaseName) {
        String result = null;
        var memberAttrValue = annotationFromBaseName.findDeclaredAttributeValue("method");
        if (member instanceof PsiMethod method) {
            var clazz = method.getContainingClass();
            if (null != clazz) {
                var classAnnotation = clazz.getAnnotation(baseName);
                if (null != classAnnotation) {
                    var classAttrValue = classAnnotation.findDeclaredAttributeValue("method");
                    if (null != classAttrValue) {
                        result = joinAttributeListValue(classAttrValue, memberAttrValue);
                    }
                }
            }
        }
        return result;
    }

    private @Nullable String joinAttributeListValue(@NotNull PsiAnnotationMemberValue classAttrValue, @Nullable PsiAnnotationMemberValue memberAttrValue) {
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
        if (attributeName.equalsIgnoreCase("method") && member instanceof PsiMethod method) {
            var attrValue = annotationFromBaseName.findDeclaredAttributeValue(attributeName);
            var attrValueUpdated = processMethodAttribute(method, annotationFromBaseName);
            if (null != attrValue) {
                var shiftOffset = 1;
                if (!attrValue.getText().contains("{")) {
                    shiftOffset = 0;
                    attrValueUpdated += "}";
                }
                return new CEImplicitAttributeInsetValue(attrValueUpdated, shiftOffset);
            }
        }
        return null;
    }
}
