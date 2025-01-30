package codeemoji.core.collector.project;

import codeemoji.core.config.CERuleElement;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.config.CERuleFeature.ANNOTATIONS;

@SuppressWarnings("UnstableApiUsage")
interface CEProject<H extends PsiModifierListOwner, A extends PsiElement> extends CEProjectConfig {

    default void processAnnotationsFR(@NotNull CERuleElement elementRule, @NotNull H evaluationElement,
                                      @NotNull A hintElement, @NotNull InlayTreeSink sink) {
        addInlayAnnotationsFR(hintElement, needsHintAnnotationsFR(elementRule, evaluationElement), sink);
    }

    default @NotNull List<String> needsHintAnnotationsFR(@NotNull CERuleElement elementRule, @NotNull H evaluationElement) {
        var rules = readRuleFeatures(elementRule);
        var featureValues = rules.get(ANNOTATIONS);
        List<String> hintValues = new ArrayList<>();
        if (null != featureValues) {
            var annotations = evaluationElement.getAnnotations();
            for (var type : annotations) {
                for (var value : featureValues) {
                    var qualifiedName = type.getQualifiedName();
                    if (null != qualifiedName && qualifiedName.equalsIgnoreCase(value)) {
                        hintValues.add(value);
                    }
                }
            }
        }
        return hintValues;
    }

    void addInlayAnnotationsFR(@NotNull A hintElement, @NotNull List<String> hintValues, @NotNull InlayTreeSink sink);

}
