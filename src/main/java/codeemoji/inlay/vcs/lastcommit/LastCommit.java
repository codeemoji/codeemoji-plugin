package codeemoji.inlay.vcs.lastcommit;

import codeemoji.core.base.CEBaseConfigurable;
import codeemoji.core.provider.CEProvider;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class LastCommit extends CEProvider<LastCommitSettings> {
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull PsiFile file, @NotNull Editor editor) {
        return new RecentlyModifiedCollector(file, editor, getKey());
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull LastCommitSettings settings) {
        return new CEBaseConfigurable<>(settings);
    }

    //screw anonymous classes. they are ugly
    private class RecentlyModifiedCollector extends VCSMethodCollector {

        protected RecentlyModifiedCollector(@NotNull PsiFile file, @NotNull Editor editor, SettingsKey<?> key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayPresentation createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            RevisionInfo lastRevision = getLastRevision(element.getProject(), textRange, getEditor(), vcsBlame);
            if (lastRevision != null) {
                return buildInlayWithEmoji(getSettings().getMainSymbol(),
                        "inlay.lastcommit.tooltip", null);
            }
            return null;
        }

        //null if it's not from last revision
        @Nullable
        private RevisionInfo getLastRevision(
                Project project, TextRange range, Editor editor, FileAnnotation blame) {
            VcsRevisionNumber lastRevision = blame.getCurrentRevision();
            if (lastRevision == null) return null;
            Document document = editor.getDocument();
            int startLine = document.getLineNumber(range.getStartOffset());
            int endLine = document.getLineNumber(range.getEndOffset());
            UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

            PrimitiveIterator.OfInt iterator = IntStream.rangeClosed(startLine, endLine)
                    .map(provider::getLineNumber).iterator();
            while (iterator.hasNext()) {
                int line = iterator.nextInt();
                var revision = blame.getLineRevisionNumber(line);
                if (lastRevision.equals(revision)) {
                    var authorProvider = getAspect(LineAnnotationAspect.AUTHOR);
                    Date date = blame.getLineDate(line);
                    if (authorProvider != null && date != null) {
                        return new RevisionInfo(authorProvider.getValue(line), date, revision);
                    }else {
                        return null;
                    }
                }
            }
            return null;
        }


    }

    private record RevisionInfo(String author, Date date, VcsRevisionNumber number) {
        public String tooltip() {
            return author + " " + date+ " " + number;
        }
    }
}








