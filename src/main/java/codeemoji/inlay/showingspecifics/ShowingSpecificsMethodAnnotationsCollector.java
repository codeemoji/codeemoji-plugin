package codeemoji.inlay.showingspecifics;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.config.CEElementRule.METHOD;
import static codeemoji.core.config.CEFeatureRule.ANNOTATIONS;

public class ShowingSpecificsMethodAnnotationsCollector extends CEMethodCollector {

    public ShowingSpecificsMethodAnnotationsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> ruleValues) {
        super(editor, mainKeyId + "." + METHOD.getValue() + "." + ANNOTATIONS.getValue(), symbol);
    }

    @Override
    public boolean isHintable(@NotNull PsiMethod element) {
        return true;
    }
}