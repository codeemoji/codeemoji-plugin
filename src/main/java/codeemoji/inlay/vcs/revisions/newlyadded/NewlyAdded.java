package codeemoji.inlay.vcs.revisions.newlyadded;

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
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class NewlyAdded extends CEProvider<NewlyAddedSettings> {

    @Override
    protected void createCollectors(CEProvider<NewlyAddedSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addMethodCollector(e -> createInlayFor(e, editor));
        builder.addClassCollector(e -> createInlayFor(e, editor));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<NewlyAddedSettings> createConfigurable() {
        return new CEBaseConfigurableWindow<>();
    }


    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiElement element, @NotNull Editor editor) {
        FileAnnotation vcsBlame = CEVcsUtils.getAnnotation(element.getContainingFile(), editor);

        if (vcsBlame == null) return null;

        Document document = CEUtils.getContainingDocument(element);
        if (document == null) return null;

        TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

        // Get HEAD revision
        VcsRevisionNumber head = CEVcsUtils.getProjectHeadRevision(element.getProject());
        if (head == null) return null;

        // Check if all lines in the method were added in HEAD
        boolean allLinesAddedInHead = allLinesMatchRevision(element.getProject(), textRange, document, vcsBlame, head);

        if (allLinesAddedInHead) {
            NewlyAddedSettings settings = getSettings();
            return InlayVisuals.translated(settings.getMainSymbol(), "inlay.newlyadded.tooltip.method");
        }

        return null;
    }


    private static boolean allLinesMatchRevision(Project project, TextRange range,
                                                 Document document, FileAnnotation blame, VcsRevisionNumber targetRevision) {

        int startLine = document.getLineNumber(range.getStartOffset());
        int endLine = document.getLineNumber(range.getEndOffset());
        UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

        return IntStream.rangeClosed(startLine, endLine)
                .map(provider::getLineNumber)
                .mapToObj(blame::getLineRevisionNumber)
                .allMatch(rev -> rev != null && rev.equals(targetRevision));
    }
}








