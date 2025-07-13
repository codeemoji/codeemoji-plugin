package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.collector.base.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LargeLineCountMethod extends CEProvider<LargeLineCountMethodSettings> {

    @Override
    protected void createCollectors(CEProvider<LargeLineCountMethodSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addSimpleMethodCollector(this::isLargeLineCountMethod);
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<LargeLineCountMethodSettings> createConfigurable() {
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
