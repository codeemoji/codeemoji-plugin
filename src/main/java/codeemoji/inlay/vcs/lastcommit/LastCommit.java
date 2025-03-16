package codeemoji.inlay.vcs.lastcommit;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class LastCommit extends CEProvider<LastCommitSettings> {

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new RecentlyModifiedCollector(psiFile, editor, getKey());
    }

    @Override
    public @NotNull CEConfigurableWindow<LastCommitSettings> createConfigurable() {
        return new LastCommitConfigurable();
    }

    private class RecentlyModifiedCollector extends VCSMethodCollector {

        protected RecentlyModifiedCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);
            Project project = element.getProject();
            VcsRevisionNumber lastRevision = CEVcsUtils.getLastGitRevision(element.getProject(),
                    element.getContainingFile().getVirtualFile(), vcs);

            RevisionInfo revisionInfo = isLastRevision(project, textRange, getEditor(), lastRevision);
            if (revisionInfo != null) {
                if (getSettings().isShowDate()) {
                    return InlayVisuals.of(getSettings().getMainSymbol(),
                            CEBundle.getString("inlay.lastcommit.tooltip.message", revisionInfo.date));
                }
                return InlayVisuals.of(getSettings().getMainSymbol(),
                        CEBundle.getString("inlay.lastcommit.tooltip"));
            }
            return null;
        }

        //null if it's not from last revision
        @Nullable
        private RevisionInfo isLastRevision(Project project, TextRange range, Editor editor, VcsRevisionNumber lastRevision) {


            if (lastRevision == null || vcsBlame == null) return null;
            if (!lastRevision.equals(vcsBlame.getCurrentRevision())) return null; //Must be last to modify this file
            Document document = editor.getDocument();
            int startLine = document.getLineNumber(range.getStartOffset());
            int endLine = document.getLineNumber(range.getEndOffset());
            UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

            PrimitiveIterator.OfInt iterator = IntStream.rangeClosed(startLine, endLine)
                    .map(provider::getLineNumber).iterator();
            while (iterator.hasNext()) {
                int line = iterator.nextInt();
                VcsRevisionNumber revision = vcsBlame.getLineRevisionNumber(line);
                if (lastRevision.equals(revision)) {
                    var authorProvider = CEVcsUtils.getAspect(vcsBlame, LineAnnotationAspect.AUTHOR);
                    Date date = vcsBlame.getLineDate(line);
                    if (authorProvider != null && date != null) {
                        return new RevisionInfo(authorProvider.getValue(line), date,
                                revision);
                    } else {
                        return null;
                    }
                }
            }
            return null;
        }


    }

    private record RevisionInfo(String author, Date date, VcsRevisionNumber number) {
        public String tooltip() {
            return author + " " + date + " " + number;
        }
    }
}








