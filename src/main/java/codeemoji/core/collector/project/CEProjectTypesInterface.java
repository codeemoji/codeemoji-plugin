package codeemoji.core.collector.project;

import codeemoji.core.collector.config.CERuleElement;
import codeemoji.core.collector.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public sealed interface CEProjectTypesInterface<A extends PsiElement> extends CEProjectConfigInterface
        permits CEProjectMethodCollector, CEProjectVariableCollector {

    default void processTypesFR(@NotNull CERuleElement elementRule, @NotNull CERuleFeature featureRule, @NotNull PsiType type,
                                @NotNull A addHintElement, @NotNull InlayHintsSink sink,
                                @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        addInlayTypesFR(addHintElement, needsHintTypesFR(elementRule, featureRule, type), sink,
                symbol, keyTooltip);
    }

    default @NotNull List<String> needsHintTypesFR(@NotNull CERuleElement elementRule, @NotNull CERuleFeature featureRule, @NotNull PsiType type) {
        var rules = readRuleFeatures(elementRule);
        var featureValues = rules.get(featureRule);
        List<String> results = new ArrayList<>();
        if (featureValues != null) {
            for (var value : featureValues) {
                String qualifiedName;
                if (type instanceof PsiClassType classType) {
                    qualifiedName = CEUtils.resolveQualifiedName(classType);
                } else {
                    qualifiedName = type.getPresentableText();
                }
                if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                    results.add(value);
                }
            }
        }
        return results;
    }

    void addInlayTypesFR(@NotNull A addHintElement, @NotNull List<String> hintValues,
                         @NotNull InlayHintsSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip);

}
