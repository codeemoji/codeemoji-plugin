package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.collector.simple.CEClassCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.LARGE_METHOD_COUNT_CLASS;

@SuppressWarnings("UnstableApiUsage")
public class LargeMethodCountClass extends CEProvider<LargeMethodCountClassSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    protected InlayHintsCollector buildCollector(Editor editor) {
        return new CEClassCollector(editor, getKey(), LARGE_METHOD_COUNT_CLASS) {
            @Override
            protected boolean needsHint(@NotNull PsiClass element){
                return isLargeMethodCountClass(element);
            }
        };
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull LargeMethodCountClassSettings settings) {
        return new LargeMethodCountClassConfigurable(settings);
    }

    private boolean isLargeMethodCountClass(PsiClass clazz){
        return clazz.getMethods().length >= getSettings().getMethodCount();
    }
}
