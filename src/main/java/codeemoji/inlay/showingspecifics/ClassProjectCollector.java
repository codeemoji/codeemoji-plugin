package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEClassProjectCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.*;

@Getter
public class ClassProjectCollector extends CEClassProjectCollector {

    public ClassProjectCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public @NotNull String getTooltipKeyAnnotations() {
        return "inlay.showingspecifics.class.annotations.tooltip";
    }

    @Override
    public @NotNull String getTooltipKeyExtends() {
        return "inlay.showingspecifics.class.extends.tooltip";
    }

    @Override
    public @NotNull String getTooltipKeyImplements() {
        return "inlay.showingspecifics.class.implements.tooltip";
    }

    @Override
    public @NotNull CESymbol getSymbolAnnotations() {
        return ANNOTATIONS_SYMBOL;
    }

    @Override
    public @NotNull CESymbol getSymbolExtends() {
        return EXTENDS_SYMBOL;
    }

    @Override
    public @NotNull CESymbol getSymbolImplements() {
        return IMPLEMENTS_SYMBOL;
    }
}