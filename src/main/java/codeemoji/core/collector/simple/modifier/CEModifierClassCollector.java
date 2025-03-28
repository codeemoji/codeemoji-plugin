package codeemoji.core.collector.simple.modifier;

import codeemoji.core.collector.simple.CESimpleReferenceClassCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.intellij.psi.PsiModifier.DEFAULT;

public final class CEModifierClassCollector extends CESimpleReferenceClassCollector {

    private final boolean activated;
    private final String modifier;

    public CEModifierClassCollector(@NotNull Editor editor, String key, Supplier<CESymbol> settings,
                                    String modifier, boolean activated) {
        super(editor, key, key + ".class." + modifier, settings);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean needsInlay(@NotNull PsiClass element){
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
