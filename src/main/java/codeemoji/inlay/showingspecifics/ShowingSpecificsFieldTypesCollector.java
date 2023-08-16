package codeemoji.inlay.showingspecifics;

import codeemoji.core.CESymbol;
import codeemoji.core.CEVariableCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.config.CEElementRule.FIELD;
import static codeemoji.core.config.CEFeatureRule.TYPES;

public class ShowingSpecificsFieldTypesCollector extends CEVariableCollector {

    public ShowingSpecificsFieldTypesCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> ruleValues) {
        super(editor, mainKeyId + "." + FIELD.getValue() + "." + TYPES.getValue(), symbol);
    }

    @Override
    public boolean isHintable(@NotNull PsiVariable element) {
        return true;
    }
}