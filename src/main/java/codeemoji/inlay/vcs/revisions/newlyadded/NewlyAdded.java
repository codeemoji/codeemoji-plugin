package codeemoji.inlay.vcs.revisions.newlyadded;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSClassCollector;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class NewlyAdded extends CEProvider<NewlyAddedSettings> {

    @Override
    protected void createCollectors(CEProvider<NewlyAddedSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        String key = getKey();
        builder.add(new NewlyAddedMethodCollector(psiFile, editor, key));
        builder.add(new NewlyAddedClassCollector(psiFile, editor, key));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<NewlyAddedSettings> createConfigurable() {
        return new CEBaseConfigurableWindow<>();
    }

    private class NewlyAddedMethodCollector extends VCSMethodCollector {

        protected NewlyAddedMethodCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;

            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            // Get HEAD revision
            VcsRevisionNumber head = CEVcsUtils.getProjectHeadRevision(element.getProject());
            if (head == null) return null;

            // Check if all lines in the method were added in HEAD
            boolean allLinesAddedInHead = allLinesMatchRevision(element.getProject(), textRange, getEditor(), vcsBlame, head);

            if (allLinesAddedInHead) {
                NewlyAddedSettings settings = getSettings();
                return InlayVisuals.translated(settings.getMainSymbol(), "inlay.newlyadded.tooltip.method");
            }

            return null;
        }
    }

    private class NewlyAddedClassCollector extends VCSClassCollector {

        protected NewlyAddedClassCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiClass element) {
            if (vcsBlame == null) return null;

            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            VcsRevisionNumber head = CEVcsUtils.getProjectHeadRevision(element.getProject());
            if (head == null) return null;

            boolean allLinesAddedInHead = allLinesMatchRevision(
                    element.getProject(), textRange, getEditor(), vcsBlame, head);

            if (allLinesAddedInHead) {
                NewlyAddedSettings settings = getSettings();
                return InlayVisuals.translated(settings.getMainSymbol(), "inlay.newlyadded.tooltip.class");
            }

            return null;
        }
    }


    private static boolean allLinesMatchRevision(Project project, TextRange range,
                                                 Editor editor, FileAnnotation blame, VcsRevisionNumber targetRevision) {

        Document document = editor.getDocument();
        int startLine = document.getLineNumber(range.getStartOffset());
        int endLine = document.getLineNumber(range.getEndOffset());
        UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

        return IntStream.rangeClosed(startLine, endLine)
                .map(provider::getLineNumber)
                .mapToObj(blame::getLineRevisionNumber)
                .allMatch(rev -> rev != null && rev.equals(targetRevision));
    }
}








