package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.simple.CEReferenceFieldCollector;
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

    public ModifierFieldCollector(@NotNull final Editor editor, @NotNull final String mainKeyId, @Nullable final CESymbol symbol,
                                  final String modifier, final boolean activated) {
        super(editor, mainKeyId + ".field." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return this.activated;
    }

    @Override
    public boolean needsHint(@NotNull final PsiField element) {
        final var psiModifierList = element.getModifierList();
        if (null != psiModifierList) {
            if (this.modifier.equalsIgnoreCase(DEFAULT)) {
                return CEUtils.checkDefaultModifier(psiModifierList);
            } else {
                return psiModifierList.hasModifierProperty(this.modifier);
            }
        }
        return false;
    }
}
