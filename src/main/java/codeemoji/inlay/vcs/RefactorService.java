package codeemoji.inlay.vcs;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
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

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service(Level.PROJECT)
public final class RefactorService {

    public static RefactorService getInstance(@NotNull Project project) {
        return project.getService(RefactorService.class);
    }


    private final Project project;
    private final GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();
    private final GitService gitService = new GitServiceImpl();

    private final Set<MethodSignature> refactoredMethods = ConcurrentHashMap.newKeySet();

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "RefactorScanThread"));
    private final AtomicReference<ScheduledFuture<?>> scheduledScan = new AtomicReference<>();

    private volatile boolean initialized = false;
    private volatile String lastScannedCommit = null;

    public RefactorService(Project project) {
        this.project = project;
        subscribeToRepoChanges();
        scheduleScan();
    }

    public boolean isRefactored(PsiMethod method) {
        if (!initialized) {
            scheduleScan();
        }
        return refactoredMethods.contains(MethodSignature.from(method));
    }

    private void subscribeToRepoChanges() {
        MessageBusConnection conn = project.getMessageBus().connect();
        conn.subscribe(GitRepository.GIT_REPO_CHANGE, (GitRepositoryChangeListener) repo -> scheduleScan());
    }

    private void scheduleScan() {
        ScheduledFuture<?> previous = scheduledScan.getAndSet(
                executor.schedule(this::runScanInBackground, 1, TimeUnit.SECONDS)
        );
        if (previous != null) {
            previous.cancel(false);
        }
    }

    private void runScanInBackground() {
        initialized = true;
        new Task.Backgroundable(project, "Analyzing refactorings", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                scanLastCommit();
            }
        }.queue();
    }

    private void scanLastCommit() {
        GitRepository repo = CEVcsUtils.getProjectGitRepository(project);
        if (repo == null) return;

        String currentSha = repo.getCurrentRevision();
        if (currentSha == null || currentSha.equals(lastScannedCommit)) return;

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
            e.printStackTrace(); // or log via Logger
        }
    }

    private void processRefactorings(List<Refactoring> refactorings) {
        for (Refactoring ref : refactorings) {
            if (ref instanceof MoveOperationRefactoring) {
                handleMove((MoveOperationRefactoring) ref);
            } else if (ref instanceof RenameOperationRefactoring) {
                handleRename((RenameOperationRefactoring) ref);
            }
        }
    }

    private void handleMove(MoveOperationRefactoring ref) {
        refactoredMethods.add(MethodSignature.from(ref.getMovedOperation()));
    }

    private void handleRename(RenameOperationRefactoring ref) {
        refactoredMethods.add(MethodSignature.from(ref.getRenamedOperation()));
    }
}
