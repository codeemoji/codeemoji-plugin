package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CESimpleReferenceFieldCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.intellij.psi.PsiModifier.DEFAULT;

public final class CEModifierFieldCollector extends CESimpleReferenceFieldCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierFieldCollector(@NotNull Editor editor, String key, Supplier<CESymbol> settings,
                                    String modifier, boolean activated) {
        super(editor, key, key + ".field." + modifier, settings);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsInlay(@NotNull PsiField element){
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
