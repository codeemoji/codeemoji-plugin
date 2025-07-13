package codeemoji.inlay.vcs.bugs;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.GitCommitCacheService;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixedIssue extends CEProvider<FixedIssueSettings> {

    @Nullable
    private FileAnnotation vcsBlame = null;

    @Override
    protected void createCollectors(CEProvider<FixedIssueSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        vcsBlame = CEVcsUtils.getAnnotation(psiFile, editor);
        builder.addMethodCollector(e -> createInlay(e, editor));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<FixedIssueSettings> createConfigurable() {
        return new FixedIssueConfigurable();
    }

    private @Nullable InlayVisuals createInlay(@NotNull PsiElement element, @NotNull Editor editor) {
        if (vcsBlame == null) return null;

        TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);
        Document document = CEUtils.getContainingDocument(element);
        if (document == null) return null;
        String matchedMessage = findFixingCommitMessage(element.getProject(), textRange, document, vcsBlame);
        if (matchedMessage != null) {
            return InlayVisuals.translated(getSettings().getMainSymbol(),
                    "inlay.fixedissue.tooltip", matchedMessage);
        }
        return null;
    }


    /**
     * Returns commit message if a fixing commit is found for any line in the given range.
     */
    private @Nullable String findFixingCommitMessage(Project project, TextRange range, Document document, FileAnnotation blame) {
        int startLine = document.getLineNumber(range.getStartOffset());
        int endLine = document.getLineNumber(range.getEndOffset());
        UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

        GitCommitCacheService gitCache = GitCommitCacheService.getInstance(project);
        var settings = getSettings();
        for (int i = startLine; i <= endLine; i++) {
            int updatedLine = provider.getLineNumber(i);
            VcsRevisionNumber revision = blame.getLineRevisionNumber(updatedLine);
            if (revision != null && gitCache.isRevisionRecent(revision, settings.getMaxRevisions())) {
                String message = gitCache.getCommitMessage(revision);
                if (message == null) {
                    continue; // Skip if message is not available
                }
                Integer fixedIssue = getIssueThatWasFixed(message);
                if (fixedIssue != null) {
                    return fixedIssue.toString();
                }
            }
        }
        return null;
    }

    // Precompiled pattern to match GitHub auto-closing issue keywords
    private static final Pattern ISSUE_CLOSING_PATTERN = Pattern.compile(
            "(?i)\\b(?:close[sd]?|fix(?:e[sd])?|resolve[sd]?)\\s+#(\\d+)\\b"
    );

    /**
     * Extracts the first issue number mentioned in a GitHub-style closing commit message.
     *
     * @param commitMessage the commit message to analyze
     * @return the first matched issue number, or null if none found
     */
    public static Integer getIssueThatWasFixed(String commitMessage) {
        if (commitMessage == null) return null;

        Matcher matcher = ISSUE_CLOSING_PATTERN.matcher(commitMessage);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return null;
    }
}








