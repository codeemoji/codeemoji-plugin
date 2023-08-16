package codeemoji.inlay.showingspecifics;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.config.CEElementRule.METHOD;
import static codeemoji.core.config.CEFeatureRule.RETURNS;

public class ShowingSpecificsMethodReturnsCollector extends CEMethodCollector {

    private final List<String> featureValues;

    public ShowingSpecificsMethodReturnsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> featureValues) {
        super(editor, mainKeyId + "." + METHOD.getValue() + "." + RETURNS.getValue(), symbol);
        this.featureValues = featureValues;
    }

    @Override
    public boolean isHintable(@NotNull PsiMethod element) {
        return false;
    }
}