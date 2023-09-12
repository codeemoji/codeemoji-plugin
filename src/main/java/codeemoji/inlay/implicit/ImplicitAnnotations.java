package codeemoji.inlay.implicit;

import codeemoji.core.collector.implicit.CEJPAEntityCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;


@Getter
@SuppressWarnings("UnstableApiUsage")
public class ImplicitAnnotations extends CEProvider<NoSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEJPAEntityCollector(editor, getKeyId(), 0x1F4AD);
    }
}
