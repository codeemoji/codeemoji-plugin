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
import gr.uom.java.xmi.diff.ExtractClassRefactoring;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.RenameClassRefactoring;
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
    private static final int SCAN_DELAY_MS = 1000;
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

    private volatile String lastScannedCommit = null;
    private volatile boolean scanInProgress = false;

    // Cached refactorings: Three separate caches for different refactoring types
    private final List<Map<MethodSignature, RenameOperationRefactoring>> renameRefactorings = new ArrayList<>();
    private final List<Map<MethodSignature, MoveOperationRefactoring>> moveRefactorings = new ArrayList<>();
    private final List<Map<ClassSignature, ExtractClassRefactoring>> extractedClassRefactorings = new ArrayList<>();
    private final List<Map<ClassSignature, RenameClassRefactoring>> renameClassRefactorings = new ArrayList<>();
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
            String newConfig = maxCommits + ":" + getCurrentCommit();
            if (!newConfig.equals(lastMaxCommitsConfig)) {
                clearCaches();
                lastScannedCommit = null;
                scheduleScan();
            }
        }
    }

    @Nullable
    public RenameOperationRefactoring getMethodRename(PsiMethod method, int maxCommits) {
        return getMethodRefactoring(method, maxCommits, renameRefactorings);
    }

    @Nullable
    public MoveOperationRefactoring getMethodMoved(PsiMethod method, int maxCommits) {
        return getMethodRefactoring(method, maxCommits, moveRefactorings);
    }

    @Nullable
    public RenameClassRefactoring getClassRenamed(PsiClass clazz, int maxCommits) {
        return getClassRefactoring(clazz, maxCommits, renameClassRefactorings);
    }

    @Nullable
    public ExtractClassRefactoring getClassExtracted(PsiClass method, int maxCommits) {
        return getClassRefactoring(method, maxCommits, extractedClassRefactorings);
    }

    private <T> T getMethodRefactoring(PsiMethod method, int maxCommits, List<Map<MethodSignature, T>> refactoringCache) {
        verifyCommitsInRangeOrScheduleScan(maxCommits);
        MethodSignature signature = MethodSignature.from(method);
        int limit = Math.min(maxCommits, refactoringCache.size());
        for (int i = 0; i < limit; i++) {
            T refactor = refactoringCache.get(i).get(signature);
            if (refactor != null) {
                return refactor;
            }
        }
        return null;
    }

    private <T> T getClassRefactoring(PsiClass clazz, int maxCommits, List<Map<ClassSignature, T>> refactoringCache) {
        verifyCommitsInRangeOrScheduleScan(maxCommits);
        ClassSignature signature = ClassSignature.from(clazz);
        int limit = Math.min(maxCommits, refactoringCache.size());
        for (int i = 0; i < limit; i++) {
            T refactor = refactoringCache.get(i).get(signature);
            if (refactor != null) {
                return refactor;
            }
        }
        return null;
    }

    private void verifyCommitsInRangeOrScheduleScan(int maxCommits) {
        if (maxCommits > this.maxCommits.get()) {
            setMaxCommits(maxCommits);
        }
        if (lastScannedCommit == null && !scanInProgress) {
            scheduleScan();
        }
    }

    private void clearCaches() {
        renameRefactorings.clear();
        moveRefactorings.clear();
        extractedClassRefactorings.clear();
    }

    private void scheduleScan() {
        String currentCommit = getCurrentCommit();
        if (currentCommit == null) return;

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

        List<String> commitHashes;
        try {
            commitHashes = GitHistoryUtils.history(project, repo.getRoot(),
                            "--max-count=" + maxCommits.get(), "--pretty=format:%H")
                    .stream().map(g -> g.getId().asString()).toList();
        } catch (Exception e) {
            LOG.warn("Failed to get commit history", e);
            return;
        }

        if (commitHashes.isEmpty()) return;

        String newestCommit = commitHashes.get(0);
        String currentConfig = maxCommits.get() + ":" + newestCommit;
        if (currentConfig.equals(lastMaxCommitsConfig)) return;

        indicator.setText("Scanning " + commitHashes.size() + " commits...");
        indicator.setFraction(0);

        // Prepare new caches
        List<Map<MethodSignature, RenameOperationRefactoring>> newRenames = new ArrayList<>();
        List<Map<MethodSignature, MoveOperationRefactoring>> newMoves = new ArrayList<>();
        List<Map<ClassSignature, ExtractClassRefactoring>> newExtractedClass = new ArrayList<>();
        List<Map<ClassSignature, RenameClassRefactoring>> newClassRenames = new ArrayList<>();

        try (Repository jgitRepo = gitService.openRepository(repo.getRoot().getCanonicalPath())) {
            for (int c = 0; c < commitHashes.size(); c++) {
                if (indicator.isCanceled()) break;

                String commitHash = commitHashes.get(c);
                Map<MethodSignature, RenameOperationRefactoring> renameMap = new HashMap<>();
                Map<MethodSignature, MoveOperationRefactoring> moveMap = new HashMap<>();
                Map<ClassSignature, ExtractClassRefactoring> extractedClassMap = new HashMap<>();
                Map<ClassSignature, RenameClassRefactoring> renameClassMap = new HashMap<>();

                newRenames.add(renameMap);
                newMoves.add(moveMap);
                newExtractedClass.add(extractedClassMap);
                newClassRenames.add(renameClassMap);

                final int commitIndex = c;
                miner.detectAtCommit(jgitRepo, commitHash, new RefactoringHandler() {
                    @Override
                    public void handle(String commitId, List<Refactoring> refactorings) {
                        if (indicator.isCanceled()) return;

                        indicator.setText2("Commit " + (commitIndex + 1) + "/" + commitHashes.size());
                        indicator.setFraction((double) (commitIndex + 1) / commitHashes.size());

                        for (Refactoring ref : refactorings) {
                            if (ref instanceof RenameOperationRefactoring rename) {
                                cacheRefactoring(renameMap, rename.getRenamedOperation(), rename);
                            } else if (ref instanceof RenameClassRefactoring rename) {
                                cacheRefactoring(renameClassMap, rename.getRenamedClass(), rename);
                            } else if (ref instanceof MoveOperationRefactoring move) {
                                cacheRefactoring(moveMap, move.getMovedOperation(), move);
                            } else if (ref instanceof ExtractClassRefactoring sigChange) {
                                cacheRefactoring(extractedClassMap, sigChange.getExtractedClass(), sigChange);
                            }
                        }
                    }

                    @Override
                    public void handleException(String commitId, Exception e) {
                        LOG.warn("Refactoring scan failed for " + commitId, e);
                        lastMaxCommitsConfig = "";
                    }
                });
            }

            if (!indicator.isCanceled()) {
                renameRefactorings.clear();
                moveRefactorings.clear();
                extractedClassRefactorings.clear();
                renameClassRefactorings.clear();

                renameRefactorings.addAll(newRenames);
                moveRefactorings.addAll(newMoves);
                extractedClassRefactorings.addAll(newExtractedClass);
                renameClassRefactorings.addAll(newClassRenames);

                lastScannedCommit = newestCommit;
                lastMaxCommitsConfig = currentConfig;

                LOG.info("Refactoring scan completed. Commits: " + commitHashes.size() +
                        ", Renames: " + countRefactorings(newRenames) +
                        ", Moves: " + countRefactorings(newMoves) +
                        ", Extracted: " + countRefactorings(newExtractedClass) +
                        ", Renamed Classes: " + countRefactorings(newClassRenames));
            }
        } catch (Exception e) {
            LOG.warn("Refactoring scan failed", e);
            lastMaxCommitsConfig = "";
        } finally {
            indicator.setFraction(1.0);
        }
    }


    private <T> void cacheRefactoring(Map<MethodSignature, T> cache, UMLOperation operation, T refactoring) {
        if (operation != null) {
            MethodSignature signature = MethodSignature.from(operation);
            if (!cache.containsKey(signature)) {
                cache.put(signature, refactoring);
            }
        }
    }

    private <T> void cacheRefactoring(Map<ClassSignature, T> cache, UMLClass operation, T refactoring) {
        if (operation != null) {
            ClassSignature signature = ClassSignature.from(operation);
            if (!cache.containsKey(signature)) {
                cache.put(signature, refactoring);
            }
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
        if (lastScannedCommit == null && !scanInProgress) {
            scheduleScan();
        }
    }
}