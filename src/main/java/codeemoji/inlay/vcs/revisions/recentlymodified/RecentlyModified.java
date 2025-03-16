package codeemoji.inlay.vcs.revisions.recentlymodified;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Objects;
import java.util.stream.IntStream;

public class RecentlyModified extends CEProvider<RecentlyModifiedSettings> {

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new RecentlyModifiedCollector(psiFile, editor, getKey());
    }

    @Override
    public @NotNull CEConfigurableWindow<RecentlyModifiedSettings> createConfigurable() {
        return new RecentlyModifiedConfigurable();
    }

    private class RecentlyModifiedCollector extends VCSMethodCollector {

        protected RecentlyModifiedCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            Date date = getEarliestModificationDate(element.getProject(), textRange, getEditor(), vcsBlame);

            if (date == null) return null;

            //check if date is within a week from now

            long diff = System.currentTimeMillis() - date.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays <= getSettings().getDays()) {
                return makePresentation(date);
            }
            return null;
        }

        private InlayVisuals makePresentation(Date date) {
            RecentlyModifiedSettings settings = getSettings();
            String tooltip = settings.isShowDate() ? date.toString() : getDaysAgoTooltipString(date);
            CESymbol mainSymbol = settings.getMainSymbol();
            return InlayVisuals.of(mainSymbol, tooltip);
        }

        private static String getDaysAgoTooltipString(Date date) {
            // calculate how many days ago it was
            long diff = System.currentTimeMillis() - date.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays == 0) {
                return CEBundle.getString("inlay.recentlymodified.tooltip.today");
            } else if (diffDays == 1) {
                return CEBundle.getString("inlay.recentlymodified.tooltip.yesterday");
            } else if (diffDays < 365) {
                return CEBundle.getString("inlay.recentlymodified.tooltip.days_ago", diffDays);
            } else {
                int years = (int) (diffDays / 365);
                return CEBundle.getString("inlay.recentlymodified.tooltip.years_ago", years);
            }
        }

        @Nullable
        private static Date getEarliestModificationDate(
                Project project, TextRange range, Editor editor, FileAnnotation blame) {

            Document document = editor.getDocument();
            int startLine = document.getLineNumber(range.getStartOffset());
            int endLine = document.getLineNumber(range.getEndOffset());
            UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

            return IntStream.rangeClosed(startLine, endLine)
                    .mapToObj(provider::getLineNumber)
                    .map(blame::getLineDate)  //gets the author name for line
                    .filter(Objects::nonNull)
                    .min(Date::compareTo)
                    .orElse(null);
        }

    }
}








