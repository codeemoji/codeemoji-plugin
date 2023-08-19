package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEVariableProjectCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.collector.project.config.CEElementRule.FIELD;
import static codeemoji.core.collector.project.config.CEElementRule.LOCALVARIABLE;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.ANNOTATIONS_SYMBOL;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.TYPES_SYMBOL;

@Getter
public class LocalVariableProjectCollector extends CEVariableProjectCollector {

    public LocalVariableProjectCollector(@NotNull Editor editor) {
        super(editor, LOCALVARIABLE);
    }

    @Override
    public @NotNull String getTooltipKeyAnnotations() {
        return "inlay.showingspecifics.localvariable.annotations.tooltip";
    }

    @Override
    public @NotNull String getTooltipKeyTypes() {
        return "inlay.showingspecifics.localvariable.types.tooltip";
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