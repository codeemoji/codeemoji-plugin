package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEElementRule;
import codeemoji.core.collector.project.config.CEFeatureRule;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CEFeatureRule.ANNOTATIONS;

public interface ICEProjectAnnotations<H extends PsiModifierListOwner, A extends PsiElement> extends ICEProjectConfigFile {

    default void processAnnotationsFR(@NotNull CEElementRule elementRule, @NotNull H evaluationElement,
                                      @NotNull A hintElement, @NotNull InlayHintsSink sink) {
        addInlayAnnotationsFR(hintElement, needsHintAnnotationsFR(elementRule, evaluationElement), sink);
    }

    default @NotNull List<String> needsHintAnnotationsFR(@NotNull CEElementRule elementRule, @NotNull H evaluationElement) {
        Map<CEFeatureRule, List<String>> rules = getRules(elementRule);
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
