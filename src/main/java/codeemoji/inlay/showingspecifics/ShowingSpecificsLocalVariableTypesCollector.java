package codeemoji.inlay.showingspecifics;

import codeemoji.core.CESymbol;
import codeemoji.core.CEVariableCollector;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.config.CEElementRule.LOCALVARIABLE;
import static codeemoji.core.config.CEFeatureRule.TYPES;

public class ShowingSpecificsLocalVariableTypesCollector extends CEVariableCollector {

    private final List<String> featureValues;

    public ShowingSpecificsLocalVariableTypesCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> featureValues) {
        super(editor, mainKeyId + "." + LOCALVARIABLE.getValue() + "." + TYPES.getValue(), symbol);
        this.featureValues = featureValues;
    }

    @Override
    public boolean isHintable(@NotNull PsiVariable element) {
        PsiTypeElement typeElement = element.getTypeElement();
        PsiType psiType = typeElement.getType();
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
        return false;
    }

    @Override
    public boolean isEnabledForParam() {
        return false;
    }

    @Override
    public boolean isEnabledForField() {
        return false;
    }
}