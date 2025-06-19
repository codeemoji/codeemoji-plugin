package codeemoji.inlay.vcs.revisions.unfrequentlymodified;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public class UnFrequentlyModified extends CEProviderMulti<UnFrequentlyModifiedSettings> {


    @Override
    public @NotNull CEConfigurableWindow<UnFrequentlyModifiedSettings> createConfigurable() {
        return new UnFrequentlyModifiedConfigurable();
    }

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(new UnFrequentlyModifiedMethodCollector(psiFile, editor, getKey()),
                new UnFrequentlyModifiedClassCollector(psiFile, editor, getKey())
        );
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

    public class UnFrequentlyModifiedClassCollector extends VCSMethodCollector {

        protected UnFrequentlyModifiedClassCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
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

    private @Nullable InlayVisuals makeInlay(Date date) {
        if (date == null) return null;

        long diff = System.currentTimeMillis() - date.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays >= getSettings().getDays()) {
            var settings = getSettings();
            String tooltip = settings.isShowDate() ? date.toString() : CEVcsUtils.getDaysAgoTooltipString(date);
            CESymbol mainSymbol = settings.getMainSymbol();
            return InlayVisuals.of(mainSymbol, tooltip);
        }
        return null;
    }
}








