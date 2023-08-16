package codeemoji.inlay.showingspecifics;

import codeemoji.core.CESymbol;
import codeemoji.core.CEVariableCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.config.CEElementRule.FIELD;
import static codeemoji.core.config.CEFeatureRule.ANNOTATIONS;

public class ShowingSpecificsFieldAnnotationsCollector extends CEVariableCollector {

    private final List<String> featureValues;

    public ShowingSpecificsFieldAnnotationsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> featureValues) {
        super(editor, mainKeyId + "." + FIELD.getValue() + "." + ANNOTATIONS.getValue(), symbol);
        this.featureValues = featureValues;
    }

    @Override
    public boolean isHintable(@NotNull PsiVariable element) {
        PsiAnnotation[] annotations = element.getAnnotations();
        for (PsiAnnotation type : annotations) {
            for (String value : featureValues) {
                String qualifiedName = type.getQualifiedName();
                if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEnabledForParam() {
        return false;
    }

    @Override
    public boolean isEnabledForLocalVariable() {
        return false;
    }
}