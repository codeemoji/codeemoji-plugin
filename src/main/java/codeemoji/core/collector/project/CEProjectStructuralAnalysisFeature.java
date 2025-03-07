package codeemoji.core.collector.project;

import codeemoji.core.config.CERuleElement;
import codeemoji.core.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CEProjectStructuralAnalysisFeature<H, A extends PsiElement> extends CEProjectConfig {

    void processStructuralAnalysisFR(@NotNull CERuleElement ruleElement,
                                     @NotNull CERuleFeature ruleFeature,
                                     @NotNull H structuralAnalysisFeatureElement,
                                     @NotNull A addHintElement,
                                     @NotNull InlayTreeSink sink,
                                     @NotNull CESymbol symbol,
                                     @NotNull String keyTooltip);

    @NotNull List<String> needsHintStructuralAnalysisFR(@NotNull CERuleElement ruleElement,
                                                        @NotNull CERuleFeature ruleFeature,
                                                        @NotNull H structuralAnalysisFeatureElement);

    void addInlayStructuralAnalysisFR(@NotNull A addHintElement,
                                      @NotNull List<String> hintValues,
                                      @NotNull InlayTreeSink sink,
                                      @NotNull CESymbol symbol,
                                      @NotNull String keyTooltip);

}
