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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service(Level.PROJECT)
public final class RefactorService implements Disposable {

    private static final Logger LOG = Logger.getInstance(RefactorService.class);
    private static final int SCAN_DELAY_MS = 1000; // 1 second delay for batching events

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

    private volatile String lastScannedCommit = null;
    private volatile boolean scanInProgress = false;

    //cached refactors
    private Map<MethodSignature, RenameOperationRefactoring> refactoredMethods = Map.of();


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

    @Nullable
    public RenameOperationRefactoring getRename(PsiMethod method) {
        if (lastScannedCommit == null && !scanInProgress) {
            scheduleScan();
        }
        return refactoredMethods.get(MethodSignature.from(method));
    }

    private void scheduleScan() {
        String currentCommit = getCurrentCommit();
        if (currentCommit == null || currentCommit.equals(lastScannedCommit)) {
            return;
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
                    scanLastCommit(indicator);
                } finally {
                    scanInProgress = false;
                }
            }
        }.queue();
    }

    private void scanLastCommit(ProgressIndicator indicator) {
        indicator.setText("Locating repository...");
        GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
        if (repo == null || indicator.isCanceled()) {
            return;
        }
        String currentSha = repo.getCurrentRevision();
        if (currentSha == null || currentSha.equals(lastScannedCommit)) {
            return;
        }

        indicator.setText("Scanning for refactorings...");
        Map<MethodSignature, RenameOperationRefactoring> newRefactoredMethods = new HashMap<>();

        try (Repository jgitRepo = gitService.openRepository(repo.getRoot().getCanonicalPath())) {

            miner.detectAtCommit(jgitRepo, currentSha, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    if (indicator.isCanceled()) return;

                    LOG.info("Processing refactorings for commit " + commitId);
                    for (Refactoring ref : refactorings) {
                        if (ref instanceof RenameOperationRefactoring ren) {
                            newRefactoredMethods.put(MethodSignature.from(ren.getRenamedOperation()), ren);
                        }
                        // Add new refactoring types here
                    }
                }

                @Override
                public void handleException(String commitId, Exception e) {
                    LOG.warn("Refactoring scan failed for commit " + commitId, e);
                    lastScannedCommit = null; // Allow retry
                }
            });

            if (!indicator.isCanceled()) {
                lastScannedCommit = currentSha;
                refactoredMethods = newRefactoredMethods;
                LOG.info("Refactoring scan completed for commit " + currentSha);
            }
        } catch (Exception e) {
            LOG.warn("Failed to scan refactorings", e);
            lastScannedCommit = null; // Allow retry
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