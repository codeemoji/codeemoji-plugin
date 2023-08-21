package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectClassCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.showingspecifics.ShowingSpecificsSymbols.*;

@Getter
public class ProjectClassCollector extends CEProjectClassCollector {

    public ProjectClassCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor, mainKeyId);
    }

    @Override
    public @NotNull CESymbol getAnnotationsSymbol() {
        return ANNOTATIONS_SYMBOL;
    }

    @Override
    public @NotNull CESymbol getExtendsSymbol() {
        return EXTENDS_SYMBOL;
    }

    @Override
    public @NotNull CESymbol getImplementsSymbol() {
        return IMPLEMENTS_SYMBOL;
    }
}