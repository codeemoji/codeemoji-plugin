package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class LargeIdentifierCountMethod extends CEProvider<LargeIdentifierCountMethodSettings> {

    @Override
    protected void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addSimpleMethodCollector(this::isLargeIdentifierCountMethod);
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<LargeIdentifierCountMethodSettings> createConfigurable() {
        return new LargeIdentifierCountMethodConfigurable();
    }

    private boolean isLargeIdentifierCountMethod(PsiMethod method) {
        return (PsiTreeUtil.collectElementsOfType(method.getBody(), PsiIdentifier.class).size()) >= getSettings().getIdentifierCount();
    }
}
