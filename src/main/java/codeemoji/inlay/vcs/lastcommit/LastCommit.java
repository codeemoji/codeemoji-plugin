package codeemoji.inlay.vcs.lastcommit;

import codeemoji.core.base.CEBaseConfigurable;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSMethodCollector;
import codeemoji.inlay.vcs.recentlymodified.RecentlyModifiedConfigurable;
import codeemoji.inlay.vcs.recentlymodified.RecentlyModifiedSettings;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
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
public class LastCommit extends CEProvider<LastCommitSettings> {
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull PsiFile file, @NotNull Editor editor) {
        return new RecentlyModifiedCollector(file, editor);
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull LastCommitSettings settings) {
        return new CEBaseConfigurable<>(settings);
    }

    //screw anonymous classes. they are ugly
    private class RecentlyModifiedCollector extends VCSMethodCollector {

        protected RecentlyModifiedCollector(@NotNull PsiFile file, @NotNull Editor editor) {
            super(file, editor);
        }

        @Override
        public InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            Date date = getEarliestModificationDate(element.getProject(), textRange, getEditor(), vcsBlame);

            if (date == null) return null;

            //check if date is within a week from now

            long diff = System.currentTimeMillis() - date.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (false) {
                return makePresentation(date);
            }
            return null;
        }

        //TODO: really refactor these stuff and merge
        private InlayPresentation makePresentation(Date d) {
            var factory = getFactory();

            CESymbol mainSymbol = getSettings().getMainSymbol();
            InlayPresentation present = mainSymbol.createPresentation(factory, false);
            present = factory.withCursorOnHover(present, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            present = factory.roundWithBackground(present);
            present = factory.withTooltip(d.toString(), present);
            return present;
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








