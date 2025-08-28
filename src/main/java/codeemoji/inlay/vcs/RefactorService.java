package codeemoji.inlay.vcs;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.diff.*;
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
import java.util.function.Function;

@Service(Level.PROJECT)
public final class RefactorService implements Disposable {
//TODO: fix ... args not creating matching method signatures
    private static final Logger LOG = Logger.getInstance(RefactorService.class);
    private static final int SCAN_DELAY_MS = 5;
    private static final int DEFAULT_MAX_COMMITS = 10;

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


    private final List<CommitRefactorings> cache = new ArrayList<>();  // New unified cache
    private volatile boolean scanInProgress = false;
    private String lastMaxCommitsConfig = "";

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

    public void setMaxCommits(int maxCommits) {
        int previous = this.maxCommits.getAndSet(maxCommits);
        if (previous != maxCommits) {
            scheduleScan();
        }
    }

    @Nullable
    public RenameOperationRefactoring getMethodRename(PsiMethod method, int maxCommits) {
        return getMethodRefactoring(method, maxCommits, cr -> cr.renameMethodMap);
    }

    @Nullable
    public RenameClassRefactoring getClassRenamed(PsiClass clazz, int maxCommits) {
        return getClassRefactoring(clazz, maxCommits, cr -> cr.renameClassMap);
    }

    @Nullable
    public MoveOperationRefactoring getMethodMoved(PsiMethod method, int maxCommits) {
        return getMethodRefactoring(method, maxCommits, cr -> cr.moveMethodMap);
    }

    @Nullable
    public ExtractOperationRefactoring getMethodExtracted(PsiMethod clazz, int maxCommits) {
        return getMethodRefactoring(clazz, maxCommits, cr -> cr.extractMethodMap);
    }

    @Nullable
    public ExtractClassRefactoring getClassExtracted(PsiClass clazz, int maxCommits) {
        return getClassRefactoring(clazz, maxCommits, cr -> cr.extractedClassMap);
    }

    private <T> T getMethodRefactoring(PsiMethod method, int maxCommits,
                                       Function<CommitRefactorings, Map<MethodSignature, T>> mapExtractor) {
        verifyCommitsInRangeOrScheduleScan(maxCommits);
        MethodSignature signature = MethodSignature.from(method);
        int limit = Math.min(maxCommits, cache.size());
        for (int i = 0; i < limit; i++) {
            T refactor = mapExtractor.apply(cache.get(i)).get(signature);
            if (refactor != null) return refactor;
        }
        return null;
    }

    private <T> T getClassRefactoring(PsiClass clazz, int maxCommits,
                                      Function<CommitRefactorings, Map<ClassSignature, T>> mapExtractor) {
        verifyCommitsInRangeOrScheduleScan(maxCommits);
        ClassSignature signature = ClassSignature.from(clazz);
        int limit = Math.min(maxCommits, cache.size());
        for (int i = 0; i < limit; i++) {
            T refactor = mapExtractor.apply(cache.get(i)).get(signature);
            if (refactor != null) return refactor;
        }
        return null;
    }

    private void verifyCommitsInRangeOrScheduleScan(int maxCommits) {
        if (maxCommits > this.maxCommits.get()) {
            setMaxCommits(maxCommits);
        }
        if (cache.isEmpty() && !scanInProgress) {
            scheduleScan();
        }
    }

    private void scheduleScan() {
        String currentCommit = getCurrentCommit();
        if (currentCommit == null) return;

        System.out.println("Scheduling refactoring scan for max commits: " + maxCommits.get());

        String currentConfig = maxCommits.get() + ":" + currentCommit;
        if (currentConfig.equals(lastMaxCommitsConfig)) return;

        ScheduledFuture<?> previous = scheduledScan.getAndSet(executor.schedule(
                this::runScanInBackground,
                SCAN_DELAY_MS,
                TimeUnit.MILLISECONDS
        ));
        if (previous != null) previous.cancel(false);
    }

    private void runScanInBackground() {
        if (scanInProgress) return;
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
        if (repo == null || indicator.isCanceled()) return;

        String currentSha = repo.getCurrentRevision();
        if (currentSha == null) return;


        List<String> newCommitHashes;
        try {
            newCommitHashes = GitHistoryUtils.history(project, repo.getRoot(),
                            "--max-count=" + maxCommits.get(), "--pretty=format:%H")
                    .stream().map(g -> g.getId().asString()).toList();
        } catch (Exception e) {
            LOG.warn("Failed to get commit history", e);
            return;
        }

        if (newCommitHashes.isEmpty()) return;

        String newestCommit = newCommitHashes.get(0);
        String currentConfig = maxCommits.get() + ":" + newestCommit;
        if (currentConfig.equals(lastMaxCommitsConfig)) return;

        // Build lookup for existing commits
        Map<String, CommitRefactorings> existingCommits = new HashMap<>();
        for (CommitRefactorings cr : cache) {
            existingCommits.put(cr.commitHash, cr);
        }

        List<CommitRefactorings> newCache = new ArrayList<>();
        int total = newCommitHashes.size();
        int scannedCount = 0;
        int reusedCount = 0;

        try (Repository jgitRepo = gitService.openRepository(repo.getRoot().getCanonicalPath())) {
            for (int i = 0; i < total; i++) {
                if (indicator.isCanceled()) break;

                String commitHash = newCommitHashes.get(i);
                CommitRefactorings cr = existingCommits.get(commitHash);

                if (cr != null) {
                    // Reuse existing results
                    newCache.add(cr);
                    reusedCount++;
                } else {
                    // Scan new commit
                    cr = new CommitRefactorings(commitHash);
                    scanCommit(jgitRepo, commitHash, cr, indicator);
                    newCache.add(cr);
                    scannedCount++;
                }

                indicator.setText2(String.format(
                        "Processing %d/%d (Scanned: %d, Reused: %d)",
                        i + 1, total, scannedCount, reusedCount
                ));
                indicator.setFraction((double) (i + 1) / total);
            }

            if (!indicator.isCanceled()) {
                cache.clear();
                cache.addAll(newCache);
                lastMaxCommitsConfig = currentConfig;

                LOG.info(String.format(
                        "Refactoring scan complete. Total: %d, Scanned: %d, Reused: %d",
                        total, scannedCount, reusedCount
                ));
            }
        } catch (Exception e) {
            LOG.warn("Refactoring scan failed", e);
            lastMaxCommitsConfig = "";
        } finally {
            indicator.setFraction(1.0);
        }
    }

    private void scanCommit(Repository repository, String commitHash,
                            CommitRefactorings results, ProgressIndicator indicator) {
        try {
            miner.detectAtCommit(repository, commitHash, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    if (indicator.isCanceled()) return;
                    for (Refactoring ref : refactorings) {
                        results.storeRefactor(ref);
                    }
                }

                @Override
                public void handleException(String commitId, Exception e) {
                    LOG.warn("Refactoring scan failed for " + commitId, e);
                }
            });
        } catch (Exception e) {
            LOG.warn("Error scanning commit " + commitHash, e);
        }
    }


    private int countRefactorings(List<? extends Map<?, ?>> maps) {
        return maps.stream().mapToInt(Map::size).sum();
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

    @ApiStatus.Internal
    public void preProcess() {
        if (cache.isEmpty() && !scanInProgress) {
            scheduleScan();
        }
    }

    // Cache storage
    private static class CommitRefactorings {
        final String commitHash;
        final Map<MethodSignature, RenameOperationRefactoring> renameMethodMap = new HashMap<>();
        final Map<ClassSignature, RenameClassRefactoring> renameClassMap = new HashMap<>();
        final Map<MethodSignature, MoveOperationRefactoring> moveMethodMap = new HashMap<>();
        final Map<ClassSignature, ExtractClassRefactoring> extractedClassMap = new HashMap<>();
        final Map<MethodSignature, ExtractOperationRefactoring> extractMethodMap = new HashMap<>();

        CommitRefactorings(String commitHash) {
            this.commitHash = commitHash;
        }

        public void storeRefactor(Refactoring ref) {
            if (ref instanceof RenameOperationRefactoring rename) {
                cacheRefactoring(this.renameMethodMap, rename.getRenamedOperation(), rename);
            } else if (ref instanceof MoveOperationRefactoring move) {
                cacheRefactoring(this.moveMethodMap, move.getMovedOperation(), move);
            } else if (ref instanceof ExtractClassRefactoring extract) {
                cacheRefactoring(this.extractedClassMap, extract.getExtractedClass(), extract);
            } else if (ref instanceof RenameClassRefactoring rename) {
                cacheRefactoring(this.renameClassMap, rename.getRenamedClass(), rename);
            } else if (ref instanceof ExtractOperationRefactoring rename) {
                cacheRefactoring(this.extractMethodMap, rename.getExtractedOperation(), rename);
            }

        }

        private <T> void cacheRefactoring(Map<MethodSignature, T> cache, UMLOperation operation, T refactoring) {
            if (operation != null) {
                MethodSignature signature = MethodSignature.from(operation);
                cache.put(signature, refactoring);
            }
        }

        private <T> void cacheRefactoring(Map<ClassSignature, T> cache, UMLClass operation, T refactoring) {
            if (operation != null) {
                ClassSignature signature = ClassSignature.from(operation);
                cache.put(signature, refactoring);
            }
        }
    }
}