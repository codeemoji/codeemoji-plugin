package codeemoji.core.collector.simple;

import codeemoji.core.collector.CECollector;
import codeemoji.core.external.CEExternalAnalyzer;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@SuppressWarnings("UnstableApiUsage")
public sealed abstract class CESimpleCollector<H extends PsiElement, A extends PsiElement> extends CECollector<A>
        permits CEClassCollector, CEMethodCollector, CEVariableCollector,
        CEReferenceClassCollector, CEReferenceFieldCollector, CEReferenceMethodCollector {

    private final InlayPresentation inlay;

    protected CESimpleCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor);
        inlay = buildInlayWithEmoji(symbol, "inlay." + keyId + ".tooltip", null);
    }

    protected final void addInlay(@Nullable A element, InlayHintsSink sink) {
        addInlayInline(element, sink, getInlay());
    }

    protected @NotNull Map<?, ?> processExternalInfo(@Nullable H element) {
        Map<?, ?> result = new HashMap<>();
        if (element != null) {
            CEExternalAnalyzer.getInstance(element.getProject()).buildExternalInfo(result, element);
        }
        return result;
    }

    protected abstract boolean needsHint(@NotNull H element, @NotNull Map<?, ?> externalInfo);
}