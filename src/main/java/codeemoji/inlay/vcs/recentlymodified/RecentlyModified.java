package codeemoji.inlay.vcs.recentlymodified;

import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsUtils;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.MenuOnClickPresentation;
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

import java.awt.*;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class RecentlyModified extends CEProvider<RecentlyModifiedSettings> {
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new RecentlyModifiedCollector(psiFile, editor, getKey());
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull RecentlyModifiedSettings settings) {
        return new RecentlyModifiedConfigurable(settings);
    }

    private class RecentlyModifiedCollector extends VCSMethodCollector {

        protected RecentlyModifiedCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull SettingsKey<?> key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayPresentation createInlayFor(@NotNull PsiMethod element) {
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

        //TODO: really refactor these stuff and merge, there are similar methods in the base ECBuilder class
        private InlayPresentation makePresentation(Date date) {
            var factory = getFactory();
            RecentlyModifiedSettings settings = getSettings();
            String tooltip = settings.isShowDate() ? date.toString() : getDaysAgoTooltipString(date);
            CESymbol mainSymbol = settings.getMainSymbol();
            InlayPresentation present = mainSymbol.createPresentation(factory, false);
            present = factory.withCursorOnHover(present, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            present = factory.roundWithBackground(present);
            present = factory.withTooltip(tooltip, present);
            // Add right-click functionality to open configuration panel
            present = addContextMenu(present, getEditor().getProject());
            return present;
        }

        //TODO: put this in a shared class
        private InlayPresentation addContextMenu(InlayPresentation presentation, Project project) {
            return new MenuOnClickPresentation(presentation, project,
                    () -> InlayHintsUtils.INSTANCE.getDefaultInlayHintsProviderPopupActions(
                            getKey(),
                            RecentlyModified.this::getName
                    )
            );

        }

        // write a method that given a Data returns a string saying how many days ago it was. Should sya "today" for today, "yesterday", x days ago and such

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








