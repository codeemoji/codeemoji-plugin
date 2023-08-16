package codeemoji.inlay.showingspecifics;

import codeemoji.core.CEClassCollector;
import codeemoji.core.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiReferenceList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.config.CEElementRule.CLASS;
import static codeemoji.core.config.CEFeatureRule.IMPLEMENTS;

public class ShowingSpecificsClassImplementsCollector extends CEClassCollector {

    public ShowingSpecificsClassImplementsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> ruleValues) {
        super(editor, mainKeyId + "." + CLASS.getValue() + "." + IMPLEMENTS.getValue(), symbol);
    }

    @Override
    public boolean isHintable(@NotNull PsiClass element) {
        PsiReferenceList list = element.getImplementsList();
        PsiClassType[] refs = list.getReferencedTypes();
        for (PsiClassType type : refs) {
            System.out.println(type);
        }
        return true;
    }
}