package codeemoji.core.provider;

import codeemoji.core.collector.CECollectorMulti;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public abstract class CEProviderMulti<S> extends CEProvider<S> {

    @Override
    public final InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CECollectorMulti(createCollectors(psiFile, editor));
    }

    protected abstract List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor);
}