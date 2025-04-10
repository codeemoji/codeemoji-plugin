package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CESimpleReferenceMethodCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class CEModifierInterfaceMethodCollector extends CESimpleReferenceMethodCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierInterfaceMethodCollector(@NotNull Editor editor, String key, Supplier<CESymbol> settings,
                                              String modifier, boolean activated) {
        super(editor, key, key + ".method." + modifier + "interface", settings);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsInlay(@NotNull PsiMethod element){
        var parent = element.getParent();
        if (parent instanceof PsiClass clazz && (clazz.isInterface())) {
            var psiModifierList = element.getModifierList();
            return psiModifierList.hasModifierProperty(modifier);

        }
        return false;
    }

}
