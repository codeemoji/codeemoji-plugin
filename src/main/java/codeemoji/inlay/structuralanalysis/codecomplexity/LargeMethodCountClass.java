package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.collector.simple.CESimpleClassCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LargeMethodCountClass extends CEProvider<LargeMethodCountClassSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleClassCollector(editor, getKey(), mainSymbol()) {
            @Override
            protected boolean needsInlay(@NotNull PsiClass element){
                return isLargeMethodCountClass(element);
            }
        };
    }

    @Override
    public @NotNull CEConfigurableWindow<LargeMethodCountClassSettings> createConfigurable() {
        return new LargeMethodCountClassConfigurable();
    }

    private boolean isLargeMethodCountClass(PsiClass clazz){
        return clazz.getMethods().length >= getSettings().getMethodCount();
    }
}
