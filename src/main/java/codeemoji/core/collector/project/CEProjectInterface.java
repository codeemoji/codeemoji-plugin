package codeemoji.core.collector.project;

import codeemoji.core.collector.config.CERuleElement;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.collector.config.CERuleFeature.ANNOTATIONS;

@SuppressWarnings("UnstableApiUsage")
sealed interface CEProjectInterface<H extends PsiModifierListOwner, A extends PsiElement>
        extends CEProjectConfigInterface permits CEProjectCollector {

    default void processAnnotationsFR(@NotNull final CERuleElement elementRule, @NotNull final H evaluationElement,
                                      @NotNull final A hintElement, @NotNull final InlayHintsSink sink) {
        this.addInlayAnnotationsFR(hintElement, this.needsHintAnnotationsFR(elementRule, evaluationElement), sink);
    }

    default @NotNull List<String> needsHintAnnotationsFR(@NotNull final CERuleElement elementRule, @NotNull final H evaluationElement) {
        final var rules = this.readRuleFeatures(elementRule);
        final var featureValues = rules.get(ANNOTATIONS);
        final List<String> hintValues = new ArrayList<>();
        if (null != featureValues) {
            final var annotations = evaluationElement.getAnnotations();
            for (final var type : annotations) {
                for (final var value : featureValues) {
                    final var qualifiedName = type.getQualifiedName();
                    if (null != qualifiedName && qualifiedName.equalsIgnoreCase(value)) {
                        hintValues.add(value);
                    }
                }
            }
        }
        return hintValues;
    }

    void addInlayAnnotationsFR(@NotNull A hintElement, @NotNull List<String> hintValues, @NotNull InlayHintsSink sink);

}
