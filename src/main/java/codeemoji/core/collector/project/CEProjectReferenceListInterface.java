package codeemoji.core.collector.project;

import codeemoji.core.collector.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.collector.config.CERuleElement.CLASS;

@SuppressWarnings("UnstableApiUsage")
public sealed interface CEProjectReferenceListInterface<H extends PsiReferenceList, A extends PsiElement>
        extends CEProjectConfigInterface permits CEProjectClassCollector {

    default void processReferenceListFR(@NotNull final CERuleFeature featureRule, @Nullable final H evaluationElement,
                                        @NotNull final A hintElement, @NotNull final InlayHintsSink sink,
                                        @NotNull final CESymbol symbol, @NotNull final String keyTooltip) {
        this.addInlayReferenceListFR(hintElement, this.needsHintReferenceListFR(featureRule, evaluationElement), sink,
                symbol, keyTooltip);
    }

    default @NotNull List<String> needsHintReferenceListFR(@NotNull final CERuleFeature featureRule, @Nullable final PsiReferenceList refList) {
        final var rules = this.readRuleFeatures(CLASS);
        final var featureValues = rules.get(featureRule);
        final List<String> hintValues = new ArrayList<>();
        if (null != featureValues && (null != refList)) {
            final var refs = refList.getReferencedTypes();
            for (final var psiType : refs) {
                for (final var value : featureValues) {
                    final var qualifiedName = CEUtils.resolveQualifiedName(psiType);
                    if (null != qualifiedName && qualifiedName.equalsIgnoreCase(value)) {
                        hintValues.add(value);
                    }
                }
            }

        }
        return hintValues;
    }

    void addInlayReferenceListFR(@NotNull A addHintElement, @NotNull List<String> hintValues,
                                 @NotNull InlayHintsSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip);

}
