package codeemoji.inlay.vcs;

import codeemoji.core.util.CEBundle;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.SyntaxTraverser;
import com.intellij.vcs.CacheableAnnotationProvider;
import git4idea.GitCommit;
import git4idea.GitRevisionNumber;
import git4idea.GitUtil;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

// static class. clean up later.
public final class CEVcsUtils {

    // cache key. Same that VcsCodeAuthorInlay uses
    private static final Key<FileAnnotation> VCS_CODE_AUTHOR_ANNOTATION = new Key<>("Vcs.CodeAuthor.Annotation");


    @Nullable
    public static LineAnnotationAspect getAspect(@Nullable FileAnnotation vcsBlame, @MagicConstant(stringValues = {
            LineAnnotationAspect.AUTHOR,
            LineAnnotationAspect.DATE,
            LineAnnotationAspect.REVISION
    }) String aspect) {
        if (vcsBlame == null) return null;
        return Arrays.stream(vcsBlame.getAspects())
                .filter(a -> Objects.equals(a.getId(), aspect))
                .findFirst().orElse(null);
    }

    // copied from VcsCodeAuthorInlayHintsCollector
    // gets the git annotation of the current file
    // basically gets the git blame for each line
    @Nullable
    public static FileAnnotation getAnnotation(AbstractVcs vcs, VirtualFile file, Editor editor) {
        // uhm get cached one maybe
        FileAnnotation annotation = editor.getUserData(VCS_CODE_AUTHOR_ANNOTATION);
        if (annotation != null) {
            return annotation;
        }

        // gets version control for this project file. Similar to GitInstance thing i guess?
        if (vcs == null) {
            return null;
        }

        // could it be this is the GitAnnotationProvider from before?
        AnnotationProvider provider = vcs.getAnnotationProvider();
        if (provider instanceof CacheableAnnotationProvider cacheable) {
            // this probably calls .annotate internally
            // .annotate is where the magic happens
            annotation = cacheable.getFromCache(file);

            //whatever this does...

            /*
            Disposable annotationDisposable = new Disposable() {
                @Override
                public void dispose() {
                    unregisterAnnotation(annotation);
                    annotation.dispose();
                }
            };

            annotation.setCloser(() -> {
                editor.putUserData(VCS_CODE_AUTHOR_ANNOTATION, null);
                Disposer.dispose(annotationDisposable);

                project.getService(AnnotationsPreloader.class).schedulePreloading(file);
            });

            annotation.setReloader(annotation::close);

            editor.putUserData(VCS_CODE_AUTHOR_ANNOTATION, annotation);
            registerAnnotation(annotation);
            disposeWithEditor(editor, annotationDisposable);
            */

            return annotation;
        }

        return null;
    }

    public static TextRange getTextRangeWithoutLeadingCommentsAndWhitespaces(PsiElement element) {
        //same as
        // InlayHintsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element)

        PsiElement startElement = SyntaxTraverser.psiApi().children(element)
                .find(child -> !(child instanceof PsiComment || child instanceof PsiWhiteSpace));
        if (startElement == null) startElement = element;
        return TextRange.create(startElement.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
    }

    // I cant find an equivalent of this using intellij vcs. This needs to return the global last revision not the latest one that modifies a certain file

    public static boolean isRevisionRecent(@NotNull Project project,
                                                  @NotNull VcsRevisionNumber revisionToCheck,
                                                  int maxRevisions) {
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);
        GitRepository repo = repositoryManager.getRepositories().stream().findFirst().orElse(null);
        if (repo == null) return false;

        List<GitCommit> recentCommits;
        try {
            recentCommits = GitHistoryUtils.history(project, repo.getRoot(), "--max-count=" + maxRevisions);
        } catch (VcsException e) {
            return false;
        }

        return recentCommits.stream()
                .map(commit -> commit.getId().asString())
                .anyMatch(rev -> rev.equals(revisionToCheck.asString()));
    }

    /**
     * Gets the latest (HEAD) revision for the current Git repo.
     */
    public static @Nullable VcsRevisionNumber getProjectHeadRevision(@NotNull Project project) {
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);
        GitRepository repo = repositoryManager.getRepositories().stream().findFirst().orElse(null);
        if (repo == null) return null;

        String hash = repo.getCurrentRevision();
        return hash != null ? new GitRevisionNumber(hash) : null;
    }

    public static @NotNull List<GitCommit> getLastCommits(@NotNull Project project, int limit) {
        GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(project);
        GitRepository repo = repositoryManager.getRepositories().stream().findFirst().orElse(null);
        if (repo == null) return List.of();

        VirtualFile root = repo.getRoot();

        try {
            // "--max-count=X" limits the number of commits returned
            return GitHistoryUtils.history(project, root, "--max-count=" + limit);
        } catch (VcsException e) {
            return List.of();
        }
    }

    /**
     * Gets the full commit message for the given revision hash.
     */
    public static @Nullable String getCommitMessageForRevision(@NotNull Project project, @NotNull String commitHash) {
        GitRepository repo = GitUtil.getRepositoryManager(project).getRepositories().stream().findFirst().orElse(null);
        if (repo == null) return null;

        try {
            //TODO: cache this
            List<GitCommit> commits = GitHistoryUtils.history(project, repo.getRoot(), commitHash);
            if (!commits.isEmpty()) {
                return commits.get(0).getFullMessage();
            }
        } catch (Exception e) {
            //errorrr!
            return null;
        }

        return null;
    }

    /**
     * Gets the full commit message for a given revision object.
     */
    public static @Nullable String getCommitMessageForRevision(@NotNull Project project, @NotNull VcsRevisionNumber revision) {
        return getCommitMessageForRevision(project, revision.asString());
    }

    @Nullable
    public static Date getEarliestModificationDate(
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

    @Nullable
    public static Date getLatestModificationDate(
            Project project, TextRange range, Editor editor, FileAnnotation blame) {

        Document document = editor.getDocument();
        int startLine = document.getLineNumber(range.getStartOffset());
        int endLine = document.getLineNumber(range.getEndOffset());
        UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

        return IntStream.rangeClosed(startLine, endLine)
                .mapToObj(provider::getLineNumber)
                .map(blame::getLineDate)  //gets the author name for line
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);
    }


    public static String getDaysAgoTooltipString(Date date) {
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

    //there's also a Vcsutil calss
}
