package codeemoji.inlay.implicit;

import codeemoji.core.collector.implicit.jpa.CEJPAEmbeddableCollector;
import codeemoji.core.collector.implicit.jpa.CEJPAEntityCollector;
import codeemoji.core.collector.implicit.spring.CESpringConfigurationCollector;
import codeemoji.core.collector.implicit.spring.CESpringControllerCollector;
import codeemoji.core.collector.implicit.spring.CESpringRestControllerCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ImplicitAnnotations extends CEProvider<ImplicitAnnotationsSettings> {

    @Override
    protected void createCollectors(Builder consumer, @NotNull PsiFile psiFile, Editor editor) {
        final int codePoint = 0x1F4AD;
        String key = getKey();
        consumer.add(new CEJPAEntityCollector(editor, key, codePoint, "javax.persistence"));
        consumer.add(new CEJPAEntityCollector(editor, key, codePoint, "jakarta.persistence"));
        consumer.add(new CEJPAEmbeddableCollector(editor, key, codePoint, "javax.persistence"));
        consumer.add(new CEJPAEmbeddableCollector(editor, key, codePoint, "jakarta.persistence"));
        consumer.add(new CESpringConfigurationCollector(editor, key, codePoint));
        consumer.add(new CESpringControllerCollector(editor, key, codePoint));
        consumer.add(new CESpringRestControllerCollector(editor, key, codePoint));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<ImplicitAnnotationsSettings> createConfigurable() {
        return new ImplicitAnnotationsConfigurable();
    }
}
