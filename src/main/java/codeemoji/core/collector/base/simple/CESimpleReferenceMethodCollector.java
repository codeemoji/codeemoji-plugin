package codeemoji.core.collector.base.simple;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.base.CEMethodCollector;
import codeemoji.core.collector.base.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class CESimpleReferenceMethodCollector extends CEReferenceMethodCollector {

    protected final Supplier<InlayVisuals> visualsProvider;

    protected CESimpleReferenceMethodCollector(@NotNull Editor editor, CEProvider<?> provider) {
        super(editor, provider.getKey());
        this.visualsProvider = () -> InlayVisuals.fromProviderSimple(provider);
    }

    @Override
    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
        return needsInlay(element) ? createInlay() : null;
    }

    @NotNull
    public InlayVisuals createInlay() {
        return visualsProvider.get();
    }

    protected abstract boolean needsInlay(@NotNull PsiMethod element);

}