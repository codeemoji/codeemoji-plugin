package codeemoji.core.collector.project;

import codeemoji.core.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.config.CERuleElement.CLASS;

@SuppressWarnings("UnstableApiUsage")
sealed interface CEProjectReferenceList<H extends PsiReferenceList, A extends PsiElement>
        extends CEProjectConfig permits CEProjectClassCollector {

    default void processReferenceListFR(@NotNull CERuleFeature featureRule, @Nullable H evaluationElement,
                                        @NotNull A hintElement, @NotNull InlayTreeSink sink,
                                        @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        addInlayReferenceListFR(hintElement, needsHintReferenceListFR(featureRule, evaluationElement), sink,
                symbol, keyTooltip);
    }

    default @NotNull List<String> needsHintReferenceListFR(@NotNull CERuleFeature featureRule, @Nullable PsiReferenceList refList) {
        var rules = readRuleFeatures(CLASS);
        var featureValues = rules.get(featureRule);
        List<String> hintValues = new ArrayList<>();
        if (null != featureValues && (null != refList)) {
            var refs = refList.getReferencedTypes();
            for (var psiType : refs) {
                for (var value : featureValues) {
                    var qualifiedName = CEUtils.resolveQualifiedName(psiType);
                    if (null != qualifiedName && qualifiedName.equalsIgnoreCase(value)) {
                        hintValues.add(value);
                    }
                }
            }

        }
        return hintValues;
    }

    void addInlayReferenceListFR(@NotNull A addHintElement, @NotNull List<String> hintValues,
                                 @NotNull InlayTreeSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip);

}
