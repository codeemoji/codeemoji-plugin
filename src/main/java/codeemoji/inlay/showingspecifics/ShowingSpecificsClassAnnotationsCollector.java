package codeemoji.inlay.showingspecifics;

import codeemoji.core.CEClassCollector;
import codeemoji.core.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.config.CEElementRule.CLASS;
import static codeemoji.core.config.CEFeatureRule.ANNOTATIONS;

public class ShowingSpecificsClassAnnotationsCollector extends CEClassCollector {

    public ShowingSpecificsClassAnnotationsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> ruleValues) {
        super(editor, mainKeyId + "." + CLASS.getValue() + "." + ANNOTATIONS.getValue(), symbol);
    }

    @Override
    public boolean isHintable(@NotNull PsiClass element) {
        PsiAnnotation[] annotations = element.getAnnotations();
        for (PsiAnnotation type : annotations) {
            System.out.println(type.getNameReferenceElement());
        }
        return true;
    }
}