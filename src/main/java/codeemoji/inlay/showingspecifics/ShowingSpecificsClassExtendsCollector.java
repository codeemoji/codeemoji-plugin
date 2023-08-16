package codeemoji.inlay.showingspecifics;

import codeemoji.core.CEClassCollector;
import codeemoji.core.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiReferenceList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.config.CEElementRule.CLASS;
import static codeemoji.core.config.CEFeatureRule.EXTENDS;

public class ShowingSpecificsClassExtendsCollector extends CEClassCollector {

    private final List<String> featureValues;

    public ShowingSpecificsClassExtendsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> featureValues) {
        super(editor, mainKeyId + "." + CLASS.getValue() + "." + EXTENDS.getValue(), symbol);
        this.featureValues = featureValues;
    }

    @Override
    public boolean isHintable(@NotNull PsiClass element) {
        PsiReferenceList list = element.getExtendsList();
        PsiClassType[] refs = list.getReferencedTypes();
        for (PsiClassType psiType : refs) {
            for (String value : featureValues) {
                String qualifiedName = CEUtils.resolveQualifiedName(psiType);
                if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }
        return false;
    }
}