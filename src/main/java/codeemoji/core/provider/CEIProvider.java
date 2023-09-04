package codeemoji.core.provider;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsProvider;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public interface CEIProvider<S> extends InlayHintsProvider<S> {

    @NotNull String getKeyId();

    InlayHintsCollector buildCollector(Editor editor);
}
