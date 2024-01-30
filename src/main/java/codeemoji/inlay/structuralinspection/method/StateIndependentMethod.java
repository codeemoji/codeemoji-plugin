package codeemoji.inlay.structuralinspection.method;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static codeemoji.inlay.structuralinspection.StructuralInspectionSymbols.STATE_DEPENDENT_METHOD;

public class StateDependentMethod extends CEProviderMulti<NoSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {
        return List.of(
                new CEMethodCollector(editor, getKeyId(), STATE_DEPENDENT_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return false;
                    }
                }
        );
    }
}
