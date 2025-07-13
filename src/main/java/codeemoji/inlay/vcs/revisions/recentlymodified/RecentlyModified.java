package codeemoji.inlay.vcs.revisions.recentlymodified;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.inlay.vcs.CEVcsUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class RecentlyModified extends CEProvider<RecentlyModifiedSettings> {

    FileAnnotation vcsBlame = null;

    @Override
    protected void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        vcsBlame = CEVcsUtils.getAnnotation(psiFile, editor);
        //builder.addMethodCollector(e -> createInlay(e, editor));
        //builder.addClassCollector(e -> createInlay(e, editor));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<RecentlyModifiedSettings> createConfigurable() {
        return new RecentlyModifiedConfigurable();
    }

    private @Nullable InlayVisuals createInlay(@NotNull PsiElement element, @NotNull Editor editor) {
        if (vcsBlame == null) return null;

        //text range of this element without comments
        TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

        Date date = CEVcsUtils.getLatestModificationDate(element.getProject(), textRange, editor, vcsBlame);

        return makeInlay(date);
    }


    private @Nullable InlayVisuals makeInlay(Date date) {
        if (date == null) return null;

        //check if date is within a week from now

        long diff = System.currentTimeMillis() - date.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        RecentlyModifiedSettings settings = getSettings();

        if (diffDays <= settings.getDays()) {
            String tooltip = settings.isShowDate() ? date.toString() : CEVcsUtils.getDaysAgoTooltipString(date);
            return InlayVisuals.direct(settings.getMainSymbol(), tooltip);
        }
        return null;
    }


}








