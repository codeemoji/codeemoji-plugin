package codeemoji.inlay.vcs.refactors;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.inlay.vcs.RefactorService;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameChanged extends CEProvider<NameChangedSettings> {

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new NameChangedCollector(psiFile, editor, getKey());
    }

    @Override
    public @NotNull CEConfigurableWindow<NameChangedSettings> createConfigurable() {
        return new CEConfigurableWindow<>();
    }

    private class NameChangedCollector extends VCSMethodCollector {

        protected NameChangedCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod method) {
            if (RefactorService.getInstance(method.getProject()).isRefactored(method)) {
                return InlayVisuals.translated(getSettings().getMainSymbol(),
                        "inlay.namechanged.tooltip", previousMethodName);
            }
            return null;
        }

    }

}








