package codeemoji.core.collector.simple;

import codeemoji.core.collector.CECollector;
import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// collector with static presentation
@Getter
@ToString
public sealed abstract class CESimpleCollector<H extends PsiElement, A extends PsiElement> extends CECollector<H, A>
        permits CESimpleClassCollector, CESimpleMethodCollector, CESimpleReferenceClassCollector, CESimpleReferenceFieldCollector, CESimpleReferenceMethodCollector, CESimpleVariableCollector {


    private final String tooltipKey;
    protected final Supplier<CESymbol> symbolGetter;

    //collector with a static presentation. Soon this wil be changed to accept a standard configuration instead
    protected CESimpleCollector(@NotNull Editor editor, String key,
                                @NotNull String tooltipKey, Supplier<CESymbol> settingsSupplier) {
        super(editor, key);
        this.tooltipKey = "inlay." + tooltipKey + ".tooltip";
        this.symbolGetter = settingsSupplier; //needed since it's not an inner class
    }

    protected CESimpleCollector(@NotNull Editor editor, String key, Supplier<CESymbol> settings) {
        this(editor, key, key, settings);
    }

    @Nullable
    @Override
    protected final InlayVisuals createInlayFor(@NotNull H element) {
        return needsInlay(element) ? createInlay() : null;
    }

    @NotNull
    public InlayVisuals createInlay() {
        return InlayVisuals.translated(symbolGetter.get(), tooltipKey, null);
    }

    protected abstract boolean needsInlay(@NotNull H element);

}