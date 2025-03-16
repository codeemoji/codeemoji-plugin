package codeemoji.inlay.vcs;

import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.psi.PsiFile;
import git4idea.GitUtil;
import git4idea.repo.GitRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class VCSMethodCollector extends CEDynamicMethodCollector {

    @Nullable
    protected final FileAnnotation vcsBlame;
    protected final AbstractVcs vcs;
    protected final ProjectLevelVcsManager projectVcs;

    protected VCSMethodCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
        super(editor, key);
        this.projectVcs = ProjectLevelVcsManager.getInstance(file.getProject());
        this.vcs = projectVcs.getVcsFor(file.getVirtualFile());
        this.vcsBlame = CEVcsUtils.getAnnotation(vcs, file.getVirtualFile(), editor);
    }

}
