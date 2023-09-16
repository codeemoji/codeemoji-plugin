package codeemoji.inlay.implicit;

import codeemoji.core.collector.implicit.jpa.CEJPAEmbeddableCollector;
import codeemoji.core.collector.implicit.jpa.CEJPAEntityCollector;
import codeemoji.core.collector.implicit.spring.CESpringConfigurationCollector;
import codeemoji.core.collector.implicit.spring.CESpringControllerCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class ImplicitAnnotations extends CEProviderMulti<NoSettings> {

    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        return new ArrayList<>(
                Arrays.asList(
                        new CEJPAEntityCollector(editor, getKeyId(), 0x1F4AD, "javax.persistence"),
                        new CEJPAEntityCollector(editor, getKeyId(), 0x1F4AD, "jakarta.persistence"),
                        new CEJPAEmbeddableCollector(editor, getKeyId(), 0x1F4AD, "javax.persistence"),
                        new CEJPAEmbeddableCollector(editor, getKeyId(), 0x1F4AD, "jakarta.persistence"),
                        new CESpringConfigurationCollector(editor, getKeyId(), 0x1F4AD),
                        new CESpringControllerCollector(editor, getKeyId(), 0x1F4AD)
                ));
    }
}
