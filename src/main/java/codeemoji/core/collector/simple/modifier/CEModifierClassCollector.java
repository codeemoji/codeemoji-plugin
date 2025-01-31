package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CEReferenceClassCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static com.intellij.psi.PsiModifier.DEFAULT;

public final class CEModifierClassCollector extends CEReferenceClassCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierClassCollector(@NotNull Editor editor, String key, @Nullable CESymbol symbol,
                                    String modifier, boolean activated) {
        super(editor, key, key + ".class." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsHint(@NotNull PsiClass element){
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
