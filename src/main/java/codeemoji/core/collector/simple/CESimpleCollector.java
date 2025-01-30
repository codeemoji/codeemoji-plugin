package codeemoji.core.collector.simple;

import codeemoji.core.collector.CECollector;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// collector with static presentation
@Getter
@ToString
@SuppressWarnings("UnstableApiUsage")
public sealed abstract class CESimpleCollector<H extends PsiElement, A extends PsiElement> extends CECollector<H, A>
        permits CEClassCollector, CESimpleMethodCollector, CEReferenceClassCollector, CEReferenceFieldCollector, CEReferenceMethodCollector, CEVariableCollector {


    private final InlayPresentation inlay;

    //collector with a static presentation. Soon this wil be changed to accept a standard configuration instead
    protected CESimpleCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key,
                                @NotNull String tooltipKey, @Nullable CESymbol symbol) {
        super(editor, key);
        this.inlay = buildInlayWithEmoji(symbol, "inlay." + tooltipKey + ".tooltip", null);
    }

    protected CESimpleCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, @Nullable CESymbol symbol) {
        this(editor, key, key.getId(), symbol);
    }

    @Nullable
    @Override
    protected final InlayPresentation createInlayFor(@NotNull H element) {
        return needsHint(element) ? inlay : null;
    }

    protected abstract boolean needsHint(@NotNull H element);


    //TODO: finish so we dont have to call create and add inaly all the time
    /*
    protected final <H2 extends PsiNamedElement> boolean maybeAddInlay(@NotNull H2 element, @NotNull InlayTreeSink InlayTreeSink) {
        var inlay = createInlay(element);
        if (null != inlay) {
            addInlayInline(element.getNameIdentifier(), InlayTreeSink, inlay);
            return true;
        }
        return false;
    }*/

}