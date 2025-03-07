package codeemoji.inlay.vcs;

import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class VCSMethodCollector extends CEDynamicMethodCollector {

    @Nullable
    protected final FileAnnotation vcsBlame;

    protected VCSMethodCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
        super(editor, key);
        this.vcsBlame = CEVcsUtils.getAnnotation(file, editor);
    }

}
