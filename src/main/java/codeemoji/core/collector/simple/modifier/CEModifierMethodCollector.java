package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

import static com.intellij.psi.PsiModifier.DEFAULT;
import static com.intellij.psi.PsiModifier.NATIVE;

public final class CEModifierMethodCollector extends CEReferenceMethodCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierMethodCollector(@NotNull Editor editor, String key,  Supplier<CEBaseSettings<?>> settings,
                                     String modifier, boolean activated) {
        super(editor, key, key + ".method." + modifier, settings);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsHint(@NotNull PsiMethod element){
        var psiModifierList = element.getModifierList();
        if (modifier.equalsIgnoreCase(DEFAULT)) {
            return CEUtils.checkDefaultModifier(psiModifierList);
        } else {
            return psiModifierList.hasModifierProperty(modifier);
        }

    }

}
