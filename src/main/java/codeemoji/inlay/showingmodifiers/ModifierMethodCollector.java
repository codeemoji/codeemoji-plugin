package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.reference.CEReferenceMethodCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.psi.PsiModifier.DEFAULT;

public class ModifierMethodCollector extends CEReferenceMethodCollector {

    private final boolean activated;
    private final String modifier;

    public ModifierMethodCollector(@NotNull Editor editor, @NotNull String mainKeyId, @Nullable CESymbol symbol,
                                   String modifier, boolean activated) {
        super(editor, mainKeyId + ".method." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsHint(@NotNull PsiMethod element) {
        var psiModifierList = element.getModifierList();
        if (modifier.equalsIgnoreCase(DEFAULT)) {
            return CEUtils.checkDefaultModifier(psiModifierList);
        } else {
            return psiModifierList.hasModifierProperty(modifier);
        }

    }

}
