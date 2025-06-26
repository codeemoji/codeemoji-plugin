package codeemoji.inlay.vcs;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import git4idea.GitCommit;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service(Service.Level.PROJECT)
public final class GitCommitCacheService {
    private static final Logger LOG = Logger.getInstance(GitCommitCacheService.class);
    private static final int MAX_RECENT_COMMITS = 100; // Default maximum recent commits to cache

    public static GitCommitCacheService getInstance(@NotNull Project project) {
        return project.getService(GitCommitCacheService.class);
    }

    private final Project project;
    private final ConcurrentMap<String, GitCommit> commitCache = new ConcurrentHashMap<>();
    private final Set<String> scheduledFetches = ConcurrentHashMap.newKeySet();
    private final List<String> recentCommitHashes = new CopyOnWriteArrayList<>(); // Thread-safe list of recent commit hashes

    public GitCommitCacheService(@NotNull Project project) {
        this.project = project;
        refreshRecentCommitsAsync(); // Initial load of recent commits
    }

    /**
     * Non-blocking. Returns cached commit or null. Schedules background fetch if needed.
     */
    public @Nullable GitCommit getCommit(@NotNull String hash) {
        GitCommit cached = commitCache.get(hash);
        if (cached == null) {
            scheduleCommitFetch(hash);
        }
        return cached;
    }

    public @Nullable GitCommit getCommit(@NotNull VcsRevisionNumber revision) {
        return getCommit(revision.asString());
    }

    public @Nullable String getCommitMessage(@NotNull VcsRevisionNumber revision) {
        GitCommit commit = getCommit(revision);
        return commit != null ? commit.getFullMessage().trim() : null;
    }

    /**
     * Non-blocking. Checks if a revision is within the last N commits (cached values only).
     * @param depth Number of recent commits to consider (max 100)
     * @return true if found in recent commits, false otherwise
     */
    public boolean isRevisionRecent(@NotNull VcsRevisionNumber revision, int depth) {
        return isCommitRecent(revision.asString(), depth);
    }

    /**
     * Non-blocking. Checks if a commit hash is within the last N commits (cached values only).
     * @param depth Number of recent commits to consider (max 100)
     */
    public boolean isCommitRecent(@NotNull String commitHash, int depth) {
        if (depth <= 0) return false;

        // Use the last min(depth, MAX_RECENT_COMMITS) commits
        int checkDepth = Math.min(depth, MAX_RECENT_COMMITS);
        int maxIndex = Math.min(checkDepth, recentCommitHashes.size());

        for (int i = 0; i < maxIndex; i++) {
            if (commitHash.equals(recentCommitHashes.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Manually preload a list of commits into the cache.
     */
    public void putCommitsInCache(@NotNull List<GitCommit> commits) {
        for (GitCommit commit : commits) {
            String hash = commit.getId().asString();
            commitCache.putIfAbsent(hash, commit);
            // Add to recent list if not already present
            if (!recentCommitHashes.contains(hash)) {
                recentCommitHashes.add(0, hash); // Add at beginning (most recent)
            }
        }
        // Trim to max size
        if (recentCommitHashes.size() > MAX_RECENT_COMMITS) {
            recentCommitHashes.subList(MAX_RECENT_COMMITS, recentCommitHashes.size()).clear();
        }
    }

    /**
     * Asynchronously refresh the list of recent commits.
     */
    public void refreshRecentCommitsAsync() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
                if (repo == null) return;

                List<GitCommit> recentCommits = GitHistoryUtils.history(
                        project,
                        repo.getRoot(),
                        "--max-count=" + MAX_RECENT_COMMITS
                );

                // Update recent commit hashes (newest first)
                List<String> hashes = new java.util.ArrayList<>(recentCommits.size());
                for (GitCommit commit : recentCommits) {
                    hashes.add(commit.getId().asString());
                }

                recentCommitHashes.clear();
                recentCommitHashes.addAll(hashes);

                // Update cache with recent commits
                putCommitsInCache(recentCommits);

            } catch (Exception e) {
                LOG.warn("Failed to refresh recent commits", e);
            }
        });
    }

    private void scheduleCommitFetch(@NotNull String commitHash) {
        if (!scheduledFetches.add(commitHash)) return;

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                GitCommit commit = fetchCommit(commitHash);
                if (commit != null) {
                    commitCache.put(commitHash, commit);
                    // Add to recent list if not present
                    if (!recentCommitHashes.contains(commitHash)) {
                        recentCommitHashes.add(0, commitHash);
                        // Trim if needed
                        if (recentCommitHashes.size() > MAX_RECENT_COMMITS) {
                            recentCommitHashes.remove(recentCommitHashes.size() - 1);
                        }
                    }
                    ApplicationManager.getApplication().invokeLater(() -> {
                        if (!project.isDisposed()) {
                            DaemonCodeAnalyzer.getInstance(project).restart();
                        }
                    });
                }
            } catch (Exception e) {
                LOG.warn("Failed to fetch commit " + commitHash, e);
            } finally {
                scheduledFetches.remove(commitHash);
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
                    "-1" // Limit to 1 commit
            );
            return commits.isEmpty() ? null : commits.get(0);
        } catch (Exception e) {
            LOG.warn("Git fetch failed for " + commitHash, e);
            return null;
        }
    }
}