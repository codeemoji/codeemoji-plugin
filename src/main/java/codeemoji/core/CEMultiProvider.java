package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public abstract class CEMultiProvider<S> extends CEProvider<S> {

    public final @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEMultiCollector(editor, buildCollectors(editor));
    }

    public abstract @Nullable List<InlayHintsCollector> buildCollectors(@NotNull Editor editor);
}