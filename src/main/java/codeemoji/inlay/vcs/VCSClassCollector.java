package codeemoji.inlay.vcs;

import codeemoji.core.collector.simple.CEDynamicClassCollector;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class VCSClassCollector extends CEDynamicClassCollector {

    @Nullable
    protected final FileAnnotation vcsBlame;
    protected final AbstractVcs vcs;
    private final ProjectLevelVcsManager projectVcs;

    protected VCSClassCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
        super(editor, key);
        this.projectVcs = ProjectLevelVcsManager.getInstance(file.getProject());
        this.vcs = projectVcs.getVcsFor(file.getVirtualFile());
        this.vcsBlame = CEVcsUtils.getAnnotation(vcs, file.getVirtualFile(), editor);
    }

}
