package codeemoji.inlay.implicit;

import codeemoji.core.collector.implicit.CEImplicit;
import codeemoji.core.collector.implicit.CEImplicitCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CESymbol;
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
        var jpaEntityImplicit = new CEImplicit("javax.persistence.Entity");
        jpaEntityImplicit.addAnnotationForField("javax.persistence.Column", new CESymbol(0x1F9D0, "@Column"), "name");
        return new CEImplicitCollector(editor, jpaEntityImplicit);
    }
}
