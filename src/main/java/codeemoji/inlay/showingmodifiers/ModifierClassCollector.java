package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.simple.CEReferenceClassCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.PsiModifier.DEFAULT;

public class ModifierClassCollector extends CEReferenceClassCollector {

    final boolean activated;
    final String modifier;

    public ModifierClassCollector(@NotNull final Editor editor, @NotNull final String mainKeyId, @Nullable final CESymbol symbol,
                                  final String modifier, final boolean activated) {
        super(editor, mainKeyId + ".class." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return this.activated;
    }

    @Override
    public boolean needsHint(@NotNull final PsiClass element) {
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
