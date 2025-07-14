package codeemoji.inlay.vcs.revisions.lastcommit;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.vcs.CEVcsUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

public class LastCommit extends CEProvider<LastCommitSettings> {

    @Override
    protected void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addMethodCollector(e -> createInlayFor(e, editor));
        builder.addClassCollector(e -> createInlayFor(e, editor));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<LastCommitSettings> createConfigurable() {
        return new LastCommitConfigurable();
    }

    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiElement element, @NotNull Editor editor) {
        FileAnnotation vcsBlame = CEVcsUtils.getAnnotation(element.getContainingFile(), editor);
        if (vcsBlame == null) return null;

        Document document = CEUtils.getContainingDocument(element);
        if (document == null) return null;

        //text range of this element without comments
        TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);
        Project project = element.getProject();
        VcsRevisionNumber lastRevision = CEVcsUtils.getProjectHeadRevision(element.getProject());

        RevisionInfo revisionInfo = isLastRevision(project, vcsBlame, document, textRange, lastRevision);
        if (revisionInfo != null) {
            if (getSettings().isShowDate()) {
                return InlayVisuals.translated(getSettings().getMainSymbol(),
                        "inlay.lastcommit.tooltip.message", revisionInfo.date);
            } else {
                return InlayVisuals.translated(getSettings().getMainSymbol(),
                        "inlay.lastcommit.tooltip");
            }
        }
        return null;
    }


    //null if it's not from last revision
    @Nullable
    private RevisionInfo isLastRevision(Project project, FileAnnotation vcsBlame,Document document,
                                        TextRange range,VcsRevisionNumber lastRevision) {

        if (lastRevision == null) return null;
        if (!lastRevision.equals(vcsBlame.getCurrentRevision())) return null; //Must be last to modify this file


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


    private record RevisionInfo(String author, Date date, VcsRevisionNumber number) {
        public String tooltip() {
            return author + " " + date + " " + number;
        }
    }
}








