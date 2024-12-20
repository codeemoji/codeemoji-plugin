package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class CEModifierInterfaceMethodCollector extends CEReferenceMethodCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierInterfaceMethodCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, @Nullable CESymbol symbol,
                                              String modifier, boolean activated) {
        super(editor, key, key.getId() + ".method." + modifier + "interface", symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsHint(@NotNull PsiMethod element){
        var parent = element.getParent();
        if (parent instanceof PsiClass clazz && (clazz.isInterface())) {
            var psiModifierList = element.getModifierList();
            return psiModifierList.hasModifierProperty(modifier);

        }
        return false;
    }

}
