package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.reference.CEReferenceMethodCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModifierInterfaceMethodCollector extends CEReferenceMethodCollector {

    private final boolean activated;
    private final String modifier;

    public ModifierInterfaceMethodCollector(@NotNull Editor editor, @NotNull String mainKeyId, @Nullable CESymbol symbol,
                                            String modifier, boolean activated) {
        super(editor, mainKeyId + ".method." + modifier + "interface", symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsHint(@NotNull PsiMethod element) {
        var parent = element.getParent();
        if (parent instanceof PsiClass clazz && (clazz.isInterface())) {
            var psiModifierList = element.getModifierList();
            return psiModifierList.hasModifierProperty(modifier);

        }
        return false;
    }

}
