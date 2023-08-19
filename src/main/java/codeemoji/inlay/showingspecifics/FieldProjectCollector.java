package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEVariableProjectCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.collector.project.config.CEElementRule.FIELD;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.ANNOTATIONS_SYMBOL;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.TYPES_SYMBOL;

@Getter
public class FieldProjectCollector extends CEVariableProjectCollector {

    public FieldProjectCollector(@NotNull Editor editor) {
        super(editor, FIELD);
    }

    @Override
    public @NotNull String getTooltipKeyAnnotations() {
        return "inlay.showingspecifics.field.annotations.tooltip";
    }

    @Override
    public @NotNull String getTooltipKeyTypes() {
        return "inlay.showingspecifics.field.types.tooltip";
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