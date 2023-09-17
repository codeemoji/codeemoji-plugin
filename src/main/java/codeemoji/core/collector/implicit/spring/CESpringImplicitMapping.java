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
    public final @Nullable String createAttributesFor(@NotNull final PsiMember member, @NotNull final PsiAnnotation memberAnnotation) {
        final List<CEImplicitAttribute> attrList = new ArrayList<>(this.createChildrenAttributesFor(member, memberAnnotation));
        attrList.add(this.processNameAttribute(member));
        if (null == memberAnnotation.findAttribute("params")) {
            this.prepareAttribute(member, memberAnnotation, "params", attrList);
        }
        if (null == memberAnnotation.findAttribute("headers")) {
            this.prepareAttribute(member, memberAnnotation, "headers", attrList);
        }
        return this.formatAttributes(memberAnnotation, attrList.toArray(new CEImplicitAttribute[0]));
    }

    public @NotNull List<CEImplicitAttribute> createChildrenAttributesFor(@NotNull final PsiMember member,
                                                                          @NotNull final PsiAnnotation memberAnnotation) {
        return new ArrayList<>();
    }

    @NotNull
    public CEImplicitAttribute processNameAttribute(@NotNull final PsiMember member) {
        final var clazz = member.getContainingClass();
        Object nameValue = member.getName();
        if (null != clazz) {
            final var classAnnotation = clazz.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
            if (null != classAnnotation) {
                final var nameClassAttr = classAnnotation.findDeclaredAttributeValue("name");
                if (null != nameClassAttr) {
                    final var nameClassAttrText = nameClassAttr.getText().replaceAll("\"", "").trim();
                    if (!nameClassAttrText.isEmpty()) {
                        nameValue = nameClassAttrText + "#" + member.getName();
                    }
                }
            }
        }
        return new CEImplicitAttribute("name", nameValue, true);
    }

    public void prepareAttribute(@NotNull final PsiMember member, @NotNull final PsiAnnotation annotationFromBaseName,
                                 @NotNull final String attributeName, @NotNull final List<CEImplicitAttribute> attrList) {
        final var processAttribute = this.processAttribute(member, annotationFromBaseName, attributeName);
        final var attribute = new CEImplicitAttribute(attributeName, processAttribute, false);
        attrList.add(attribute);
    }

    private @Nullable String processAttribute(@NotNull final PsiMember member, @NotNull final PsiAnnotation annotationFromBaseName,
                                              @NotNull final String attributeName) {
        String result = null;
        final var memberAttrValue = annotationFromBaseName.findDeclaredAttributeValue(attributeName);
        if (member instanceof final PsiMethod method) {
            final var clazz = method.getContainingClass();
            if (null != clazz) {
                final var classAnnotation = clazz.getAnnotation("org.springframework.web.bind.annotation.RequestMapping");
                if (null != classAnnotation) {
                    final var classAttrValue = classAnnotation.findDeclaredAttributeValue(attributeName);
                    if (null != classAttrValue) {
                        result = this.joinAttributeListValue(classAttrValue, memberAttrValue);
                    }
                }
            }
        }
        return result;
    }

    private @Nullable String joinAttributeListValue(@NotNull final PsiAnnotationMemberValue classAttrValue,
                                                    @Nullable final PsiAnnotationMemberValue memberAttrValue) {
        final var textClass = classAttrValue.getText();
        final var textMember = null != memberAttrValue ? memberAttrValue.getText() : "";
        final var textClassCompare = textClass.replaceAll("\\{", "").replaceAll("\\}", "").replaceAll(" ", "");
        final var textMemberCompare = textMember.replaceAll("\\{", "").replaceAll("\\}", "").replaceAll(" ", "");
        final var classMethods = textClassCompare.split(",");
        final var memberMethods = textMemberCompare.split(",");
        final var classSet = new HashSet<>(Arrays.asList(classMethods));
        final var memberSet = new HashSet<>(Arrays.asList(memberMethods));
        final var mixedSet = new HashSet<>();
        mixedSet.addAll(classSet);
        mixedSet.addAll(memberSet);
        mixedSet.removeAll(memberSet);
        final var setList = new ArrayList<>(mixedSet);
        Collections.reverse(setList);
        final var mixedArray = setList.toArray();
        final var mixedArrayValue = Arrays.toString(mixedArray).replaceAll("\\[", "").replaceAll("]", "");
        if (textMemberCompare.isEmpty()) {
            if ("{}".equals(textClass)) {
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
    public @Nullable String createAnnotationFor(@NotNull final PsiMember member) {
        return null;
    }

    @Override
    public @Nullable CEImplicitAttributeInsetValue updateAttributesFor(@NotNull final PsiMember member, @NotNull final PsiAnnotation memberAnnotation, @NotNull final String attributeName) {
        if (member instanceof final PsiMethod method) {
            final var attrValue = memberAnnotation.findDeclaredAttributeValue(attributeName);
            var attrValueUpdated = this.processAttribute(method, memberAnnotation, attributeName);
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