package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.reference.CEReferenceFieldCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.PsiModifier.DEFAULT;

public class ModifierFieldCollector extends CEReferenceFieldCollector {

    final boolean activated;
    final String modifier;

    public ModifierFieldCollector(@NotNull Editor editor, @NotNull String mainKeyId, @Nullable CESymbol symbol,
                                  String modifier, boolean activated) {
        super(editor, mainKeyId + ".field." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsHint(@NotNull PsiField element) {
        var psiModifierList = element.getModifierList();
        if (psiModifierList != null) {
            if (modifier.equalsIgnoreCase(DEFAULT)) {
                return CEUtils.checkDefaultModifier(psiModifierList);
            } else {
                return psiModifierList.hasModifierProperty(modifier);
            }
        }
        return false;
    }
}
