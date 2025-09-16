package codeemoji.inlay.vcs;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.GitCommit;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service(Service.Level.PROJECT)
public final class GitCommitCacheService implements Disposable {
    private static final Logger LOG = Logger.getInstance(GitCommitCacheService.class);
    private static final int MAX_RECENT_COMMITS = 100;
    private static final int REFRESH_DELAY_MS = 100;
    private final MessageBusConnection messageBusConnection;

    public static GitCommitCacheService getInstance(@NotNull Project project) {
        return project.getService(GitCommitCacheService.class);
    }

    private final Project project;
    private final ConcurrentMap<String, GitCommit> commitCache = new ConcurrentHashMap<>();
    private final Set<String> scheduledFetches = ConcurrentHashMap.newKeySet();
    private final List<String> recentCommitHashes = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<ScheduledFuture<?>> scheduledRefresh = new AtomicReference<>();
    private volatile String lastRefreshedHead = "";

    public GitCommitCacheService(@NotNull Project project) {
        this.project = project;
        this.messageBusConnection = project.getMessageBus().connect(this);
        this.messageBusConnection.subscribe(GitRepository.GIT_REPO_CHANGE,
                (GitRepositoryChangeListener) repo -> {
                    if (repo.getRoot().equals(getRepoRoot())) {
                        scheduleRefresh();
                    }
                });
        scheduleRefresh(); // Initial scan
    }

    public @Nullable GitCommit getCommit(@NotNull String hash) {
        GitCommit cached = commitCache.get(hash);
        if (cached == null) scheduleFetch(hash);
        return cached;
    }

    public @Nullable GitCommit getCommit(@NotNull VcsRevisionNumber revision) {
        return getCommit(revision.asString());
    }

    public @Nullable String getCommitMessage(@NotNull VcsRevisionNumber revision) {
        GitCommit commit = getCommit(revision);
        return commit != null ? commit.getFullMessage().trim() : null;
    }

    public boolean isCommitRecent(@NotNull String hash, int depth) {
        int limit = Math.min(depth, MAX_RECENT_COMMITS);
        for (int i = 0; i < limit && i < recentCommitHashes.size(); i++) {
            if (hash.equals(recentCommitHashes.get(i))) return true;
        }
        return false;
    }

    public boolean isRevisionRecent(@NotNull VcsRevisionNumber revision, int depth) {
        return isCommitRecent(revision.asString(), depth);
    }

    public void putCommitsInCache(@NotNull List<GitCommit> commits) {
        for (GitCommit commit : commits) {
            String hash = commit.getId().asString();
            commitCache.putIfAbsent(hash, commit);
        }

        // Update recent list
        List<String> hashes = commits.stream()
                .map(c -> c.getId().asString())
                .toList();

        synchronized (recentCommitHashes) {
            recentCommitHashes.clear();
            recentCommitHashes.addAll(hashes.subList(0, Math.min(MAX_RECENT_COMMITS, hashes.size())));
        }
    }

    private void scheduleFetch(@NotNull String commitHash) {
        if (!scheduledFetches.add(commitHash)) return;

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                GitCommit commit = fetchCommit(commitHash);
                if (commit != null) {
                    commitCache.put(commitHash, commit);
                    synchronized (recentCommitHashes) {
                        if (!recentCommitHashes.contains(commitHash)) {
                            recentCommitHashes.add(0, commitHash);
                            if (recentCommitHashes.size() > MAX_RECENT_COMMITS) {
                                recentCommitHashes.remove(recentCommitHashes.size() - 1);
                            }
                        }
                    }
                    restartDaemonAnalyzer();
                }
            } catch (Exception e) {
                LOG.warn("Failed to fetch commit " + commitHash, e);
            } finally {
                scheduledFetches.remove(commitHash);
            }
        });
    }

    private void scheduleRefresh() {
        ScheduledFuture<?> existing = scheduledRefresh.getAndSet(
                executor.schedule(this::refreshRecentCommits, REFRESH_DELAY_MS, TimeUnit.MILLISECONDS)
        );
        if (existing != null) existing.cancel(false);
    }

    private void refreshRecentCommits() {
        new Task.Backgroundable(project, "Refreshing recent commits", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
                if (repo == null || indicator.isCanceled()) return;

                String currentHead = repo.getCurrentRevision();
                if (currentHead == null || currentHead.equals(lastRefreshedHead)) return;

                try {
                    List<GitCommit> recentCommits = GitHistoryUtils.history(
                            project,
                            repo.getRoot(),
                            "--max-count=" + MAX_RECENT_COMMITS
                    );

                    if (!recentCommits.isEmpty()) {
                        putCommitsInCache(recentCommits);
                        lastRefreshedHead = currentHead;
                        restartDaemonAnalyzer();
                        LOG.info("Refreshed recent commits at HEAD " + currentHead);
                    }
                } catch (Exception e) {
                    LOG.warn("Failed to refresh recent commits", e);
                }
            }
        }.queue();
    }

    private void restartDaemonAnalyzer() {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (!project.isDisposed()) {
                DaemonCodeAnalyzer.getInstance(project).restart();
            }
        });
    }

    private @Nullable GitCommit fetchCommit(@NotNull String commitHash) {
        GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
        if (repo == null) return null;
        try {
            List<GitCommit> commits = GitHistoryUtils.history(
                    project,
                    repo.getRoot(),
                    commitHash,
                    "-1"
            );
            return commits.isEmpty() ? null : commits.get(0);
        } catch (Exception e) {
            LOG.warn("Git fetch failed for " + commitHash, e);
            return null;
        }
    }

    private @Nullable VirtualFile getRepoRoot() {
        GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
        return repo != null ? repo.getRoot() : null;
    }


    @Override
    public void dispose() {
        messageBusConnection.dispose();

        ScheduledFuture<?> scheduled = scheduledRefresh.getAndSet(null);
        if (scheduled != null) scheduled.cancel(false);

        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
