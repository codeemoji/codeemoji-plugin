package codeemoji.inlay.showingmodifiers;

import codeemoji.core.CEClassReferenceCollector;
import codeemoji.core.CESymbol;
import codeemoji.core.CEUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.PsiModifier.DEFAULT;

public class ClassModifierCollector extends CEClassReferenceCollector {

    final boolean activated;
    final String modifier;

    public ClassModifierCollector(@NotNull Editor editor, @Nullable CESymbol symbol,
                                  String modifier, boolean activated) {
        super(editor, "showingmodifiers.class." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean isHintable(@NotNull PsiClass element) {
        PsiModifierList psiModifierList = element.getModifierList();
        if (psiModifierList != null) {
            if (modifier.equalsIgnoreCase(DEFAULT)) {
                return CEUtil.checkDefaultModifier(psiModifierList);
            } else {
                return psiModifierList.hasModifierProperty(modifier);
            }
        }
        return false;
    }
}
