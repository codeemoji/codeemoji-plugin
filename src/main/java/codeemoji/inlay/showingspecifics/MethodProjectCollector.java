package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEMethodProjectCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.ANNOTATIONS_SYMBOL;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.RETURNS_SYMBOL;

@Getter
public class MethodProjectCollector extends CEMethodProjectCollector {

    public MethodProjectCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public @NotNull String getTooltipKeyAnnotations() {
        return "inlay.showingspecifics.method.annotations.tooltip";
    }

    @Override
    public @NotNull String getTooltipKeyReturns() {
        return "inlay.showingspecifics.method.returns.tooltip";
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