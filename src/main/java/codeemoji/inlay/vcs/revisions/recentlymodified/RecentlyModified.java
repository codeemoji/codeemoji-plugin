package codeemoji.inlay.vcs.revisions.recentlymodified;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.RefactorManager;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public class RecentlyModified extends CEProviderMulti<RecentlyModifiedSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(new RecentlyModifiedMethodCollector(psiFile, editor, getKey()),
                new RecentlyModifiedClassCollector(psiFile, editor, getKey()));
    }

    @Override
    public @NotNull CEConfigurableWindow<RecentlyModifiedSettings> createConfigurable() {
        return new RecentlyModifiedConfigurable();
    }

    private class RecentlyModifiedMethodCollector extends VCSMethodCollector {

        protected RecentlyModifiedMethodCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            Date date = CEVcsUtils.getEarliestModificationDate(element.getProject(), textRange, getEditor(), vcsBlame);

            return makeInlay(date);
        }
    }

    private class RecentlyModifiedClassCollector extends VCSMethodCollector {

        protected RecentlyModifiedClassCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            Date date = CEVcsUtils.getEarliestModificationDate(element.getProject(), textRange, getEditor(), vcsBlame);

            return makeInlay(date);
        }
    }

    private @Nullable InlayVisuals makeInlay(Date date) {
        if (date == null) return null;

        //check if date is within a week from now

        long diff = System.currentTimeMillis() - date.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays <= getSettings().getDays()) {
            RecentlyModifiedSettings settings = getSettings();
            String tooltip = settings.isShowDate() ? date.toString() : CEVcsUtils.getDaysAgoTooltipString(date);
            CESymbol mainSymbol = settings.getMainSymbol();
            return InlayVisuals.of(mainSymbol, tooltip);
        }
        return null;
    }


}








