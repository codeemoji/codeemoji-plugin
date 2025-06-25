package codeemoji.inlay.vcs;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.util.messages.MessageBusConnection;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryChangeListener;
import gr.uom.java.xmi.diff.MoveOperationRefactoring;
import gr.uom.java.xmi.diff.RenameOperationRefactoring;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RefactorManager {
    private final Project project;
    private final GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
    private final GitService gitService = new GitServiceImpl(); //utility class for git operations
    private final Set<MethodSignature> refactoredMethods = new HashSet<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<ScheduledFuture<?>> scheduledScan = new AtomicReference<>();

    private volatile boolean initialized = false;
    private volatile String lastScannedCommit = null;

    public RefactorManager(Project project) {
        this.project = project;
        subscribeToRepoChanges();
        scheduleScan(); // initial scan
    }

    /**
     * Public API: check if a method was refactored in the latest commit
     */
    public boolean isRefactored(PsiMethod method) {
        // ensure at least one scan has been scheduled
        if (!initialized) {
            scheduleScan();
        }
        return refactoredMethods.contains(MethodSignature.from(method));
    }

    /**
     * Listen to Git repository changes (branch switch, new commits)
     */
    private void subscribeToRepoChanges() {
        MessageBusConnection conn = project.getMessageBus().connect();
        conn.subscribe(GitRepository.GIT_REPO_CHANGE, (GitRepositoryChangeListener) repo -> scheduleScan());
    }

    /**
     * Debounced scheduling of a background refactoring scan
     */
    private void scheduleScan() {
        // cancel any pending scan
        ScheduledFuture<?> previous = scheduledScan.getAndSet(
                executor.schedule(this::runScanInBackground, 1, TimeUnit.SECONDS)
        );
        if (previous != null) {
            previous.cancel(false);
        }
    }

    /**
     * Launches the scan on a background thread
     */
    private void runScanInBackground() {
        initialized = true;
        new Task.Backgroundable(project, "Analyzing refactorings", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                scanLastCommit();
            }
        }.queue();
    }

    /**
     * Core scan logic: only re-run if HEAD changed
     */
    private void scanLastCommit() {
        GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
        if (repo == null) return;

        String currentSha = repo.getCurrentRevision();
        if (currentSha == null || currentSha.equals(lastScannedCommit)) {
            return;
        }

        // prepare for new scan
        lastScannedCommit = currentSha;
        refactoredMethods.clear();

        try (Repository jgitRepo = gitService.openRepository(repo.getRoot().getCanonicalPath())) {

            miner.detectAtCommit(jgitRepo, currentSha, new RefactoringHandler() {
                @Override
                public void handle(String commitId, List<Refactoring> refactorings) {
                    processRefactorings(refactorings);
                }
            });
        } catch (Exception e) {
            // logging; avoid spamming UI
            e.printStackTrace();
        }
    }

    /**
     * Dispatch refactoring types to specific handlers
     */
    private void processRefactorings(List<Refactoring> refactorings) {
        for (Refactoring ref : refactorings) {
            if (ref instanceof MoveOperationRefactoring) {
                handleMove((MoveOperationRefactoring) ref);
            } else if (ref instanceof RenameOperationRefactoring) {
                handleRename((RenameOperationRefactoring) ref);
            }
            // TODO: support other refactoring types here
        }
    }

    private void handleMove(MoveOperationRefactoring ref) {
        refactoredMethods.add(MethodSignature.from(ref.getMovedOperation()));
    }

    private void handleRename(RenameOperationRefactoring ref) {
        refactoredMethods.add(MethodSignature.from(ref.getRenamedOperation()));
    }

}
