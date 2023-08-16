package codeemoji.inlay.showingspecifics;

import codeemoji.core.CESymbol;
import codeemoji.core.CEVariableCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.enums.CEElementRule.FIELD;
import static codeemoji.core.enums.CEFeatureRule.ANNOTATIONS;

public class ShowingSpecificsFieldAnnotationsCollector extends CEVariableCollector {

    public ShowingSpecificsFieldAnnotationsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> ruleValues) {
        super(editor, mainKeyId + "." + FIELD.getValue() + "." + ANNOTATIONS.getValue(), symbol);
    }

    @Override
    public boolean isHintable(@NotNull PsiVariable element) {
        return true;
    }
}