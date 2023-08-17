package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.basic.CEMethodCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.collector.project.config.CEElementRule.METHOD;
import static codeemoji.core.collector.project.config.CEFeatureRule.RETURNS;

public class ShowingSpecificsMethodReturnsCollector extends CEMethodCollector {

    private final List<String> featureValues;

    public ShowingSpecificsMethodReturnsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> featureValues) {
        super(editor, mainKeyId + "." + METHOD.getValue() + "." + RETURNS.getValue(), symbol);
        this.featureValues = featureValues;
    }

    @Override
    public boolean isHintable(@NotNull PsiMethod element) {
        if (!element.isConstructor()) {
            PsiType psiType = element.getReturnType();
            for (String value : featureValues) {
                String qualifiedName = "";
                if (psiType instanceof PsiClassType classType) {
                    qualifiedName = CEUtils.resolveQualifiedName(classType);
                } else {
                    qualifiedName = psiType.getPresentableText();
                }
                if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}