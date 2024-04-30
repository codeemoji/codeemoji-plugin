package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.LARGE_LINE_COUNT_METHOD;

@SuppressWarnings("UnstableApiUsage")
public class LargeLineCountMethod extends CEProvider<LargeLineCountMethodSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    protected InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), LARGE_LINE_COUNT_METHOD) {
            @Override
            protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return isLargeLineCountMethod(element);
            }
        };
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull LargeLineCountMethodSettings settings) {
        return new LargeLineCountMethodConfigurable(settings);
    }

    private boolean isLargeLineCountMethod(PsiMethod method) {
        int methodLineCount = CEUtils.calculateMethodBodyLineCount(method);
        if(getSettings().isCommentExclusionApplied()){
            methodLineCount = methodLineCount - CEUtils.calculateCommentPaddingLinesInMethod(method);
        }
        return method.getBody() != null && methodLineCount >= getSettings().getLinesOfCode();
    }
}
