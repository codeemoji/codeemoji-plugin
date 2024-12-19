package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.intellij.psi.PsiModifier.DEFAULT;

public final class CEModifierMethodCollector extends CEReferenceMethodCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierMethodCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, @Nullable CESymbol symbol,
                                     String modifier, boolean activated) {
        super(editor, key, key.getId() + ".method." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
        var psiModifierList = element.getModifierList();
        if (modifier.equalsIgnoreCase(DEFAULT)) {
            return CEUtils.checkDefaultModifier(psiModifierList);
        } else {
            return psiModifierList.hasModifierProperty(modifier);
        }

    }

}
