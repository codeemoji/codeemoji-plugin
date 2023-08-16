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
import static codeemoji.core.config.CEFeatureRule.EXTENDS;

public class ShowingSpecificsClassExtendsCollector extends CEClassCollector {

    public ShowingSpecificsClassExtendsCollector(@NotNull Editor editor, @NotNull String mainKeyId, CESymbol symbol, List<String> ruleValues) {
        super(editor, mainKeyId + "." + CLASS.getValue() + "." + EXTENDS.getValue(), symbol);
    }

    @Override
    public boolean isHintable(@NotNull PsiClass element) {
        PsiReferenceList list = element.getExtendsList();
        PsiClassType[] refs = list.getReferencedTypes();
        for (PsiClassType type : refs) {
            System.out.println(type);
        }
        return true;
    }
}