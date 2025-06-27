package codeemoji.core.collector.base.simple;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.base.CEMethodCollector;
import codeemoji.core.collector.base.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class CESimpleVariableCollector extends CEVariableCollector {

    protected final Supplier<InlayVisuals> visualsProvider;

    protected CESimpleVariableCollector(@NotNull Editor editor, CEProvider<?> provider) {
        super(editor, provider.getKey());
        this.visualsProvider = () -> InlayVisuals.fromProviderSimple(provider);
    }

    @Override
    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiVariable element) {
        return needsInlay(element) ? createInlay() : null;
    }

    @NotNull
    public InlayVisuals createInlay() {
        return visualsProvider.get();
    }

    protected abstract boolean needsInlay(@NotNull PsiVariable element);

}