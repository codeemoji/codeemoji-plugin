package codeemoji.core.provider;

import codeemoji.core.collector.CECollectorMulti;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract class CEProviderMulti<S> extends CEProvider<S> {

    @Override
    @Contract("_ -> new")
    public final @NotNull InlayHintsCollector buildCollector(Editor editor) {
        return new CECollectorMulti(buildCollectors(editor));
    }

    public abstract List<InlayHintsCollector> buildCollectors(Editor editor);
}