package codeemoji.inlay.showingmodifiers;

import codeemoji.core.CEMethodCallCollector;
import codeemoji.core.CESymbol;
import codeemoji.core.CEUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.PsiModifier.DEFAULT;

public class MethodModifierCollector extends CEMethodCallCollector {

    private final boolean activated;
    private final String modifier;

    public MethodModifierCollector(@NotNull Editor editor, @Nullable CESymbol symbol,
                                   String modifier, boolean activated) {
        super(editor, "showingmodifiers.method." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean isHintable(@NotNull PsiMethod element) {
        PsiModifierList psiModifierList = element.getModifierList();
        if (modifier.equalsIgnoreCase(DEFAULT)) {
            return CEUtil.checkDefaultModifier(psiModifierList);
        } else {
            return psiModifierList.hasModifierProperty(modifier);
        }

    }

}
