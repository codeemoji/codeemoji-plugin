package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.LARGE_IDENTIFIER_COUNT_METHOD;

@SuppressWarnings("UnstableApiUsage")
public class LargeIdentifierCountMethod extends CEProvider<LargeIdentifierCountMethodSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    protected InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), LARGE_IDENTIFIER_COUNT_METHOD) {
            @Override
            protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isLargeIdentifierCountMethod(element);
            }
        };
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull LargeIdentifierCountMethodSettings settings) {
        return new LargeIdentifierCountMethodConfigurable(settings);
    }
    
    private boolean isLargeIdentifierCountMethod(PsiMethod method){
        return (PsiTreeUtil.collectElementsOfType(method.getBody(), PsiIdentifier.class).size()) >= getSettings().getIdentifierCount();
    }
}
