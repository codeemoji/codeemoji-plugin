package codeemoji.core.collector.base.simple;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.base.CEReferenceClassCollector;
import codeemoji.core.collector.base.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class CESimpleReferenceClassCollector extends CEReferenceClassCollector {

    protected final Supplier<InlayVisuals> visualsProvider;

    protected CESimpleReferenceClassCollector(@NotNull Editor editor, CEProvider<?> provider) {
        super(editor, provider.getKey());
        this.visualsProvider = () -> InlayVisuals.fromProviderSimple(provider);
    }

    @Override
    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiClass element) {
        return needsInlay(element) ? createInlay() : null;
    }

    @NotNull
    public InlayVisuals createInlay() {
        return visualsProvider.get();
    }

    protected abstract boolean needsInlay(@NotNull PsiClass element);

}