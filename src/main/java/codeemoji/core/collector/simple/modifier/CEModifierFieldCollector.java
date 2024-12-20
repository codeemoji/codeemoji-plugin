package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CEReferenceFieldCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.intellij.psi.PsiModifier.DEFAULT;

public final class CEModifierFieldCollector extends CEReferenceFieldCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierFieldCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, @Nullable CESymbol symbol,
                                    String modifier, boolean activated) {
        super(editor, key, key.getId() + ".field." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsHint(@NotNull PsiField element){
        var psiModifierList = element.getModifierList();
        if (null != psiModifierList) {
            if (modifier.equalsIgnoreCase(DEFAULT)) {
                return CEUtils.checkDefaultModifier(psiModifierList);
            } else {
                return psiModifierList.hasModifierProperty(modifier);
            }
        }
        return false;
    }
}
