package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectVariableCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.collector.project.config.CEElementRule.LOCALVARIABLE;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.ANNOTATIONS_SYMBOL;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.TYPES_SYMBOL;

@Getter
public class ProjectLocalVariableCollector extends CEProjectVariableCollector {

    public ProjectLocalVariableCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor, LOCALVARIABLE, mainKeyId);
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