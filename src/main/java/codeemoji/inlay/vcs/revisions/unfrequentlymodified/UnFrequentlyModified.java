package codeemoji.inlay.vcs.revisions.unfrequentlymodified;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSClassCollector;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public class UnFrequentlyModified extends CEProvider<UnFrequentlyModifiedSettings> {


    @Override
    public @NotNull CEBaseConfigurableWindow<UnFrequentlyModifiedSettings> createConfigurable() {
        return new UnFrequentlyModifiedConfigurable();
    }

    @Override
    protected void createCollectors(CEProvider<UnFrequentlyModifiedSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        String key = getKey();
        builder.addIf(getSettings().appliesToMethods(), new UnFrequentlyModifiedMethodCollector(psiFile, editor, key));
        builder.addIf(getSettings().appliesToClasses(), new UnFrequentlyModifiedClassCollector(psiFile, editor, key));
    }

    private class UnFrequentlyModifiedMethodCollector extends VCSMethodCollector {

        protected UnFrequentlyModifiedMethodCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            Date date = CEVcsUtils.getLatestModificationDate(element.getProject(), textRange, getEditor(), vcsBlame);

            return makeInlay(date);
        }
    }

    public class UnFrequentlyModifiedClassCollector extends VCSClassCollector {

        protected UnFrequentlyModifiedClassCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiClass element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            Date date = CEVcsUtils.getLatestModificationDate(element.getProject(), textRange, getEditor(), vcsBlame);

            return makeInlay(date);
        }
    }

    private @Nullable InlayVisuals makeInlay(Date date) {
        if (date == null) return null;

        long diff = System.currentTimeMillis() - date.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays >= getSettings().getDays()) {
            var settings = getSettings();
            String tooltip = settings.isShowDate() ? date.toString() : CEVcsUtils.getDaysAgoTooltipString(date);
            CESymbol mainSymbol = settings.getMainSymbol();
            return InlayVisuals.direct(mainSymbol, tooltip);
        }
        return null;
    }
}








