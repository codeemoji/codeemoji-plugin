package codeemoji.inlay.showingmodifiers;

import codeemoji.core.CEFieldReferenceCollector;
import codeemoji.core.CESymbol;
import codeemoji.core.CEUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.PsiModifier.DEFAULT;

public class FieldModifierCollector extends CEFieldReferenceCollector {

    final boolean activated;
    final String modifier;

    public FieldModifierCollector(@NotNull Editor editor, @Nullable CESymbol symbol,
                                  String modifier, boolean activated) {
        super(editor, "showingmodifiers.field." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean isHintable(@NotNull PsiField element) {
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
