package codeemoji.core.provider;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsProvider;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public sealed interface CEIProvider<S> extends InlayHintsProvider<S> permits CEProvider {

    @NotNull String getKeyId();

    InlayHintsCollector buildCollector(Editor editor);
}
