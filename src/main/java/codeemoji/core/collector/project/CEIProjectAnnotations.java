package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEProjectRuleElement;
import codeemoji.core.collector.project.config.CEProjectRuleFeature;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CEProjectRuleFeature.ANNOTATIONS;

public interface CEIProjectAnnotations<H extends PsiModifierListOwner, A extends PsiElement> extends CEIProjectConfigFile {

    default void processAnnotationsFR(@NotNull CEProjectRuleElement elementRule, @NotNull H evaluationElement,
                                      @NotNull A hintElement, @NotNull InlayHintsSink sink) {
        addInlayAnnotationsFR(hintElement, needsHintAnnotationsFR(elementRule, evaluationElement), sink);
    }

    default @NotNull List<String> needsHintAnnotationsFR(@NotNull CEProjectRuleElement elementRule, @NotNull H evaluationElement) {
        Map<CEProjectRuleFeature, List<String>> rules = readRules(elementRule);
        List<String> featureValues = rules.get(ANNOTATIONS);
        List<String> hintValues = new ArrayList<>();
        if (featureValues != null) {
            PsiAnnotation[] annotations = evaluationElement.getAnnotations();
            for (PsiAnnotation type : annotations) {
                for (String value : featureValues) {
                    String qualifiedName = type.getQualifiedName();
                    if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                        hintValues.add(value);
                    }
                }
            }
        }
        return hintValues;
    }

    void addInlayAnnotationsFR(@NotNull A hintElement, @NotNull List<String> hintValues, @NotNull InlayHintsSink sink);

}