package codeemoji.inlay.invisiblefeatures;

import codeemoji.core.CEMethodCallCollector;
import codeemoji.core.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MethodInterfaceModifierCollector extends CEMethodCallCollector {

    private final boolean activated;
    private final String modifier;

    public MethodInterfaceModifierCollector(@NotNull Editor editor, @Nullable CESymbol symbol,
                                            String modifier, boolean activated) {
        super(editor, modifier + "methodinterfacemodifier", symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean isHintable(@NotNull PsiMethod element) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiClass clazz) {
            if (clazz.isInterface()) {
                PsiModifierList psiModifierList = element.getModifierList();
                return psiModifierList.hasModifierProperty(modifier);
            }
        }
        return false;
    }

}
