package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.LARGE_IDENTIFIER_COUNT_METHOD;

public class LargeIdentifierCountMethod extends CEProvider<LargeIdentifierCountMethodSettings> {

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
            @Override
            protected boolean needsInlay(@NotNull PsiMethod element){
                return isLargeIdentifierCountMethod(element);
            }
        };
    }

    @Override
    public @NotNull CEConfigurableWindow<LargeIdentifierCountMethodSettings> createConfigurable() {
        return new LargeIdentifierCountMethodConfigurable();
    }
    
    private boolean isLargeIdentifierCountMethod(PsiMethod method){
        return (PsiTreeUtil.collectElementsOfType(method.getBody(), PsiIdentifier.class).size()) >= getSettings().getIdentifierCount();
    }
}
