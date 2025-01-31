package codeemoji.inlay.implicit;

import codeemoji.core.collector.implicit.jpa.CEJPAEmbeddableCollector;
import codeemoji.core.collector.implicit.jpa.CEJPAEntityCollector;
import codeemoji.core.collector.implicit.spring.CESpringConfigurationCollector;
import codeemoji.core.collector.implicit.spring.CESpringControllerCollector;
import codeemoji.core.collector.implicit.spring.CESpringRestControllerCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ImplicitAnnotations extends CEProviderMulti<ImplicitAnnotationsSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        final int codePoint = 0x1F4AD;
        String key = getKey();
        return new ArrayList<>(
                Arrays.asList(
                        new CEJPAEntityCollector(editor, key, codePoint, "javax.persistence"),
                        new CEJPAEntityCollector(editor, key, codePoint, "jakarta.persistence"),
                        new CEJPAEmbeddableCollector(editor, key, codePoint, "javax.persistence"),
                        new CEJPAEmbeddableCollector(editor, key, codePoint, "jakarta.persistence"),
                        new CESpringConfigurationCollector(editor, key, codePoint),
                        new CESpringControllerCollector(editor, key, codePoint),
                        new CESpringRestControllerCollector(editor, key, codePoint)
                ));
    }

    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull CEConfigurableWindow<ImplicitAnnotationsSettings> createConfigurable() {
        return new ImplicitAnnotationsConfigurable();
    }
}
