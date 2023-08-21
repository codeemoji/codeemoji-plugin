package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectVariableCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.collector.project.config.CEProjectRuleElement.LOCALVARIABLE;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsSymbols.ANNOTATIONS_SYMBOL;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsSymbols.TYPES_SYMBOL;

@Getter
public class ProjectLocalVariableCollector extends CEProjectVariableCollector {

    public ProjectLocalVariableCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor, LOCALVARIABLE, mainKeyId);
    }

    @Override
    public @NotNull CESymbol getAnnotationsSymbol() {
        return ANNOTATIONS_SYMBOL;
    }

    @Override
    public @NotNull CESymbol getTypesSymbol() {
        return TYPES_SYMBOL;
    }
}