package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class LargeMethodCountClass extends CEProvider<LargeMethodCountClassSettings> {

    @Override
    protected void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addSimpleClassCollector(this::isLargeMethodCountClass);
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<LargeMethodCountClassSettings> createConfigurable() {
        return new LargeMethodCountClassConfigurable();
    }

    private boolean isLargeMethodCountClass(PsiClass clazz) {
        return clazz.getMethods().length >= getSettings().getMethodCount();
    }
}
