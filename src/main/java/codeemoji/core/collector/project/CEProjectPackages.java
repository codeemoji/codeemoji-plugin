package codeemoji.core.collector.project;

import codeemoji.core.config.CERuleElement;
import codeemoji.core.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackageStatement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

sealed interface CEProjectPackages<A extends PsiElement> extends CEProjectStructuralAnalysisFeature<PsiPackageStatement, A>
permits CEProjectMethodCollector{

    @Override
    default void processStructuralAnalysisFR(@NotNull CERuleElement ruleElement, @NotNull CERuleFeature ruleFeature, @NotNull PsiPackageStatement structuralAnalysisFeatureElement, @NotNull A addHintElement, @NotNull InlayTreeSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        addInlayStructuralAnalysisFR(addHintElement, needsHintStructuralAnalysisFR(ruleElement, ruleFeature, structuralAnalysisFeatureElement), sink, symbol, keyTooltip);
    }

    @Override
    @NotNull
    default List<String> needsHintStructuralAnalysisFR(@NotNull CERuleElement ruleElement, @NotNull CERuleFeature ruleFeature, @NotNull PsiPackageStatement structuralAnalysisFeatureElement) {
        var rules = readRuleFeatures(ruleElement);
        var featureValues = rules.get(ruleFeature);
        List<String> results = new ArrayList<>();
        if (null != featureValues) {
            for (var value : featureValues) {
                String qualifiedName = structuralAnalysisFeatureElement.getPackageName();
                if (null != qualifiedName && qualifiedName.equalsIgnoreCase(value)) {
                    results.add(value);
                }
            }
        }
        return results;
    }
}
