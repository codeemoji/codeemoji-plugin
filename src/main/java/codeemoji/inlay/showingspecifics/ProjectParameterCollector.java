package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectVariableCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.collector.project.config.CEElementRule.PARAMETER;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsSymbols.ANNOTATIONS_SYMBOL;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsSymbols.TYPES_SYMBOL;

@Getter
public class ProjectParameterCollector extends CEProjectVariableCollector {

    public ProjectParameterCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor, PARAMETER, mainKeyId);
    }

    @Override
    public @NotNull CESymbol getSymbolAnnotations() {
        return ANNOTATIONS_SYMBOL;
    }

    @Override
    public @NotNull CESymbol getSymbolTypes() {
        return TYPES_SYMBOL;
    }
}