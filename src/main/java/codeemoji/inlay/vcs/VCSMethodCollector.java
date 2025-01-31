package codeemoji.inlay.vcs;

import codeemoji.core.collector.CECollector;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.recentlymodified.RecentlyModified;
import codeemoji.inlay.vcs.recentlymodified.RecentlyModifiedSettings;
import com.intellij.codeInsight.hints.InlayHintsUtils;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.MenuOnClickPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.psi.PsiFile;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public abstract class VCSMethodCollector extends CEDynamicMethodCollector {

    @Nullable
    protected final FileAnnotation vcsBlame;
    protected VCSMethodCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
        super(editor, key);
        this.vcsBlame = CEVcsUtils.getAnnotation(file, editor);
    }
//commit kast modivier or mstcmmo or most recent
    //modified by many people
    // owneed by just 1 people
    // animal emoji
    @Nullable
    protected LineAnnotationAspect getAspect(@MagicConstant(stringValues = {
            LineAnnotationAspect.AUTHOR,
            LineAnnotationAspect.DATE,
            LineAnnotationAspect.REVISION
    }) String aspect) {
        return Arrays.stream(vcsBlame.getAspects())
                .filter(a -> Objects.equals(a.getId(), aspect))
                .findFirst().orElse(null);
    }

}
