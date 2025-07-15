package codeemoji.inlay.vcs.refactors.extract;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.inlay.vcs.RefactorService;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MovedRefactor extends CEProvider<MovedRefactorSettings> {

    @Override
    protected void createCollectors(CEProvider<MovedRefactorSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        // initialize service
        builder.addMethodCollector(this::isMethodMoved);
        builder.addClassCollector(this::isClassMoved);
    }

    @Nullable
    private InlayVisuals isClassMoved(PsiClass psiClass) {
        RefactorService instance = RefactorService.getInstance(psiClass.getProject());
        var settings = getSettings();
        var ref = instance.getClassExtracted(psiClass ,settings.getMaxRevisions());
        if (ref != null) {
            return InlayVisuals.translated(getSettings().getMainSymbol(),
                    "inlay.moverefactor.tooltip.class");
        }
        return null;
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<MovedRefactorSettings> createConfigurable() {
        return new MovedRefactorConfigurable();
    }

    protected @Nullable InlayVisuals isMethodMoved(@NotNull PsiMethod method) {
        RefactorService instance = RefactorService.getInstance(method.getProject());
        var settings = getSettings();
        var ref = instance.getMethodMoved(method ,settings.getMaxRevisions());
        if (ref != null) {
            return InlayVisuals.translated(getSettings().getMainSymbol(),
                    "inlay.moverefactor.tooltip.method");
        }
        return null;
    }

}








