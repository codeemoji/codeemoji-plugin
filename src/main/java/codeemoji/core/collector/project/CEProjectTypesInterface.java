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

    default void processTypesFR(@NotNull final CERuleElement elementRule, @NotNull final CERuleFeature featureRule, @NotNull final PsiType type,
                                @NotNull final A addHintElement, @NotNull final InlayHintsSink sink,
                                @NotNull final CESymbol symbol, @NotNull final String keyTooltip) {
        this.addInlayTypesFR(addHintElement, this.needsHintTypesFR(elementRule, featureRule, type), sink,
                symbol, keyTooltip);
    }

    default @NotNull List<String> needsHintTypesFR(@NotNull final CERuleElement elementRule, @NotNull final CERuleFeature featureRule, @NotNull final PsiType type) {
        final var rules = this.readRuleFeatures(elementRule);
        final var featureValues = rules.get(featureRule);
        final List<String> results = new ArrayList<>();
        if (null != featureValues) {
            for (final var value : featureValues) {
                final String qualifiedName;
                if (type instanceof final PsiClassType classType) {
                    qualifiedName = CEUtils.resolveQualifiedName(classType);
                } else {
                    qualifiedName = type.getPresentableText();
                }
                if (null != qualifiedName && qualifiedName.equalsIgnoreCase(value)) {
                    results.add(value);
                }
            }
        }
        return results;
    }

    void addInlayTypesFR(@NotNull A addHintElement, @NotNull List<String> hintValues,
                         @NotNull InlayHintsSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip);

}
