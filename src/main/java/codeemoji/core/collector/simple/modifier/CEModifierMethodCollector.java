package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CESimpleReferenceMethodCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.intellij.psi.PsiModifier.DEFAULT;

public final class CEModifierMethodCollector extends CESimpleReferenceMethodCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierMethodCollector(@NotNull Editor editor, String key,  Supplier<CESymbol> symbol,
                                     String modifier, boolean activated) {
        super(editor, key, key + ".method." + modifier, symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsInlay(@NotNull PsiMethod element){
        var psiModifierList = element.getModifierList();
        if (modifier.equalsIgnoreCase(DEFAULT)) {
            return CEUtils.checkDefaultModifier(psiModifierList);
        } else {
            return psiModifierList.hasModifierProperty(modifier);
        }

    }

}
