package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectMethodCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.showingspecifics.ShowingSpecificsSymbols.ANNOTATIONS_SYMBOL;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsSymbols.RETURNS_SYMBOL;

@Getter
public class ProjectMethodCollector extends CEProjectMethodCollector {

    public ProjectMethodCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor, mainKeyId);
    }

    @Override
    public @NotNull CESymbol getSymbolAnnotations() {
        return ANNOTATIONS_SYMBOL;
    }

    @Override
    public @NotNull CESymbol getSymbolReturns() {
        return RETURNS_SYMBOL;
    }

}