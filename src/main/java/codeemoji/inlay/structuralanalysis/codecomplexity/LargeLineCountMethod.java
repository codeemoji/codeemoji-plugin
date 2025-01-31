package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.LARGE_LINE_COUNT_METHOD;

public class LargeLineCountMethod extends CEProvider<LargeLineCountMethodSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), this::getSettings) {
            @Override
            protected boolean needsHint(@NotNull PsiMethod element) {
                return isLargeLineCountMethod(element);
            }
        };
    }

    @Override
    public @NotNull CEConfigurableWindow<LargeLineCountMethodSettings> createConfigurable() {
        return new LargeLineCountMethodConfigurable();
    }

    private boolean isLargeLineCountMethod(PsiMethod method) {
        int methodLineCount = CEUtils.calculateMethodBodyLineCount(method);
        if(getSettings().isCommentExclusionApplied()){
            methodLineCount = methodLineCount - CEUtils.calculateCommentPaddingLinesInMethod(method);
        }
        return method.getBody() != null && methodLineCount >= getSettings().getLinesOfCode();
    }
}
