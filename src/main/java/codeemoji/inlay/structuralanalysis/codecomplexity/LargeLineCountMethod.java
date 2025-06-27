package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.collector.base.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LargeLineCountMethod extends CEProvider<LargeLineCountMethodSettings> {

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, this) {
            @Override
            protected boolean needsInlay(@NotNull PsiMethod element) {
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
