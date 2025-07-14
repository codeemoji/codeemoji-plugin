package codeemoji.inlay.vcs.refactors;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.inlay.vcs.RefactorService;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameChanged extends CEProvider<NameChangedSettings> {

    @Override
    protected void createCollectors(CEProvider<NameChangedSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        // initialize service
        RefactorService.getInstance(psiFile.getProject()).preProcess(); //TODO: move out of here
        builder.addMethodCollector(this::createInlayFor);
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<NameChangedSettings> createConfigurable() {
        return new NameChangedConfigurable();
    }

    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod method) {
        RefactorService instance = RefactorService.getInstance(method.getProject());
        var settings = getSettings();
        var ref = instance.getMethodRename(method ,settings.getMaxRevisions());
        if (ref != null) {
            return InlayVisuals.translated(getSettings().getMainSymbol(),
                    "inlay.namechanged.tooltip", ref.getOriginalOperation().getName());
        }
        return null;
    }

}








