package codeemoji.inlay.showingspecifics;

import codeemoji.core.CESymbol;
import codeemoji.core.CEVariableCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.enums.CEElementRule.LOCALVARIABLE;
import static codeemoji.core.enums.CEFeatureRule.TYPES;

public class ShowingSpecificsLocalVariableTypesCollector extends CEVariableCollector {

    public ShowingSpecificsLocalVariableTypesCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> ruleValues) {
        super(editor, mainKeyId + "." + LOCALVARIABLE.getValue() + "." + TYPES.getValue(), symbol);
    }

    @Override
    public boolean isHintable(@NotNull PsiVariable element) {
        return true;
    }
}