package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.reference.CEMethodReferenceCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MethodInterfaceModifierCollector extends CEMethodReferenceCollector {

    private final boolean activated;
    private final String modifier;

    public MethodInterfaceModifierCollector(@NotNull Editor editor, @NotNull String mainKeyId, @Nullable CESymbol symbol,
                                            String modifier, boolean activated) {
        super(editor, mainKeyId + ".method." + modifier + "interface", symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean checkHint(@NotNull PsiMethod element) {
        PsiElement parent = element.getParent();
        if (parent instanceof PsiClass clazz && (clazz.isInterface())) {
            PsiModifierList psiModifierList = element.getModifierList();
            return psiModifierList.hasModifierProperty(modifier);

        }
        return false;
    }

}
