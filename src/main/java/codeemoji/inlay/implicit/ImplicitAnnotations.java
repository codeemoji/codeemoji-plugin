package codeemoji.inlay.implicit;

import codeemoji.core.collector.implicit.jpa.CEJPAEmbeddableCollector;
import codeemoji.core.collector.implicit.jpa.CEJPAEntityCollector;
import codeemoji.core.collector.implicit.spring.CESpringConfigurationCollector;
import codeemoji.core.collector.implicit.spring.CESpringControllerCollector;
import codeemoji.core.collector.implicit.spring.CESpringRestControllerCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class ImplicitAnnotations extends CEProviderMulti<ImplicitAnnotationsSettings> {

    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        final int codePoint = 0x1F4AD;
        String keyId = getKeyId();
        return new ArrayList<>(
                Arrays.asList(
                        new CEJPAEntityCollector(editor, keyId, codePoint, "javax.persistence"),
                        new CEJPAEntityCollector(editor, keyId, codePoint, "jakarta.persistence"),
                        new CEJPAEmbeddableCollector(editor, keyId, codePoint, "javax.persistence"),
                        new CEJPAEmbeddableCollector(editor, keyId, codePoint, "jakarta.persistence"),
                        new CESpringConfigurationCollector(editor, keyId, codePoint),
                        new CESpringControllerCollector(editor, keyId, codePoint),
                        new CESpringRestControllerCollector(editor, keyId, codePoint)
                ));
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ImplicitAnnotationsSettings settings) {
        return new ImplicitAnnotationsConfigurable(settings);
    }
}
