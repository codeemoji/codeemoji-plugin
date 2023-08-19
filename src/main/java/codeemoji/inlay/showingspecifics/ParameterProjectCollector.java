package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEVariableProjectCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.collector.project.config.CEElementRule.PARAMETER;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.ANNOTATIONS_SYMBOL;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.TYPES_SYMBOL;

@Getter
public class ParameterProjectCollector extends CEVariableProjectCollector {

    public ParameterProjectCollector(@NotNull Editor editor) {
        super(editor, PARAMETER);
    }

    @Override
    public @NotNull String getTooltipKeyAnnotations() {
        return "inlay.showingspecifics.parameter.annotations.tooltip";
    }

    @Override
    public @NotNull String getTooltipKeyTypes() {
        return "inlay.showingspecifics.parameter.types.tooltip";
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