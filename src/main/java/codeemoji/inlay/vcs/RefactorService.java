package codeemoji.inlay.vcs;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service(Level.PROJECT)
public final class RefactorService implements Disposable {

    private static final Logger LOG = Logger.getInstance(RefactorService.class);
    private static final int SCAN_DELAY_MS = 1000; // 1 second delay for batching events
    private static final int DEFAULT_MAX_COMMITS = 10; // Default scan depth

    public static RefactorService getInstance(@NotNull Project project) {
        return project.getService(RefactorService.class);
    }

    private final Project project;
    private final GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
    private final GitService gitService = new GitServiceImpl();
    private final MessageBusConnection messageBusConnection;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
            r -> new Thread(r, "RefactorScanThread")
    );
    private final AtomicReference<ScheduledFuture<?>> scheduledScan = new AtomicReference<>();
    private final AtomicInteger maxCommits = new AtomicInteger(DEFAULT_MAX_COMMITS);

    private volatile String lastScannedCommit = null;
    private volatile boolean scanInProgress = false;

    // Cached refactors
    private final List<Map<MethodSignature, RenameOperationRefactoring>> refactoredMethods = new ArrayList<>();
    private String lastMaxCommitsConfig = ""; // Tracks maxCommits configuration state

    public RefactorService(Project project) {
        this.project = project;
        this.messageBusConnection = project.getMessageBus().connect(this);
        this.messageBusConnection.subscribe(GitRepository.GIT_REPO_CHANGE,
                (GitRepositoryChangeListener) repo -> {
                    if (repo.getRoot().equals(getRepoRoot())) {
                        scheduleScan();
                    }
                });
    }

    /**
     * Set the maximum number of commits to scan for refactorings
     */
    public void setMaxCommits(int maxCommits) {
        int previous = this.maxCommits.getAndSet(maxCommits);

        // If the value actually changed
        if (previous != maxCommits) {
            String newConfig = maxCommits + ":" + getCurrentCommit();

            // If we've scanned with a different configuration, invalidate cache
            if (!newConfig.equals(lastMaxCommitsConfig)) {
                refactoredMethods.clear(); // Clear cache
                lastScannedCommit = null; // Force rescan
                scheduleScan();
            }
        }
    }

    @Nullable
    public RenameOperationRefactoring getRename(PsiMethod method, int maxCommits) {
        if (maxCommits > this.maxCommits.get()) {
            setMaxCommits(maxCommits);
        }
        if (lastScannedCommit == null && !scanInProgress) {
            scheduleScan();
        }
        MethodSignature signature = MethodSignature.from(method);
        for (int i = 0; i < Math.min(maxCommits,  refactoredMethods.size()); i++) {
            Map<MethodSignature, RenameOperationRefactoring> methods = refactoredMethods.get(i);
            RenameOperationRefactoring refactor = methods.get(signature);
            if (refactor != null) {
                return refactor;
            }
        }
        return null;
    }

    private void scheduleScan() {
        String currentCommit = getCurrentCommit();
        if (currentCommit == null) {
            return;
        }

        // Check if we need to rescan based on configuration changes
        String currentConfig = maxCommits.get() + ":" + currentCommit;
        if (currentConfig.equals(lastMaxCommitsConfig)) {
            return; // Already scanned with this config
        }

        ScheduledFuture<?> previous = scheduledScan.getAndSet(executor.schedule(
                this::runScanInBackground,
                SCAN_DELAY_MS,
                TimeUnit.MILLISECONDS
        ));

        if (previous != null) {
            previous.cancel(false);
        }
    }

    private void runScanInBackground() {
        if (scanInProgress) {
            return;
        }

        new Task.Backgroundable(project, "Analyzing refactorings", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                scanInProgress = true;
                try {
                    scanRecentCommits(indicator);
                } finally {
                    scanInProgress = false;
                }
            }
        }.queue();
    }

    private void scanRecentCommits(ProgressIndicator indicator) {
        indicator.setText("Locating repository...");
        GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
        if (repo == null || indicator.isCanceled()) {
            return;
        }

        String currentSha = repo.getCurrentRevision();
        if (currentSha == null) {
            return;
        }
        // Get commit history
        List<String> commitHashes;
        try {
            commitHashes = GitHistoryUtils.history(project, repo.getRoot(),
                            "--max-count=" + maxCommits.get(), "--pretty=format:%H")
                    .stream().map(g -> g.getId().asString()).toList();
        } catch (Exception e) {
            LOG.warn("Failed to get commit history", e);
            return;
        }

        if (commitHashes.isEmpty()) {
            return;
        }

        String newestCommit = commitHashes.get(0);
        String oldestCommit = commitHashes.get(commitHashes.size() - 1);
        String currentConfig = maxCommits.get() + ":" + newestCommit;

        if (currentConfig.equals(lastMaxCommitsConfig)) {
            return; // Already scanned with this config
        }

        indicator.setText("Scanning " + commitHashes.size() + " commits for refactorings...");
        indicator.setFraction(0);
        List<Map<MethodSignature, RenameOperationRefactoring>> newRefactoredMethods = new ArrayList<>();

        try (Repository jgitRepo = gitService.openRepository(repo.getRoot().getCanonicalPath())) {
            for (int c = 0; c< commitHashes.size(); c++) {
                String commitHash = commitHashes.get(c);
                Map<MethodSignature, RenameOperationRefactoring> map = new HashMap<>();
                 newRefactoredMethods.add(map);

                miner.detectAtCommit(jgitRepo, commitHash, new RefactoringHandler() {
                    private int processedCommits = 0;

                    @Override
                    public void handle(String commitId, List<Refactoring> refactorings) {
                        if (indicator.isCanceled()) return;

                        indicator.setText2("Processing commit " + processedCommits + "/" + commitHashes.size());
                        indicator.setFraction((double) processedCommits / commitHashes.size());

                        processedCommits++;

                        LOG.debug("Processing refactorings for commit " + commitId);
                        for (Refactoring ref : refactorings) {
                            if (ref instanceof RenameOperationRefactoring ren) {
                                MethodSignature signature = MethodSignature.from(ren.getRenamedOperation());
                                if (!map.containsKey(signature)) {
                                    map.put(signature, ren);
                                }
                            }
                        }
                    }

                    @Override
                    public void handleException(String commitId, Exception e) {
                        LOG.warn("Refactoring scan failed for commit " + commitId, e);
                        lastMaxCommitsConfig = ""; // Allow retry
                    }
                });
            }
            if (!indicator.isCanceled()) {
                lastScannedCommit = newestCommit;
                lastMaxCommitsConfig = currentConfig;
                refactoredMethods.clear();
                refactoredMethods.addAll(newRefactoredMethods);
                LOG.info("Scanned " + commitHashes.size() + " commits. Found " +
                        newRefactoredMethods.size() + " method renames.");
            }
        } catch (Exception e) {
            LOG.warn("Failed to scan refactorings between commits", e);
            lastMaxCommitsConfig = ""; // Allow retry
        } finally {
            indicator.setFraction(1.0);
        }
    }

    private String getCurrentCommit() {
        GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
        return repo != null ? repo.getCurrentRevision() : null;
    }

    private VirtualFile getRepoRoot() {
        GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
        return repo != null ? repo.getRoot() : null;
    }

    @Override
    public void dispose() {
        messageBusConnection.disconnect();

        ScheduledFuture<?> scheduled = scheduledScan.getAndSet(null);
        if (scheduled != null) {
            scheduled.cancel(false);
        }

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

    @ApiStatus.Internal
    public void preProcess() {
        if (lastScannedCommit == null && !scanInProgress) {
            scheduleScan();
        }
    }
}