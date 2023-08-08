package codeemoji.inlay.invisiblefeatures;

import codeemoji.core.CEMethodCallCollector;
import codeemoji.core.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MethodModifierCollector extends CEMethodCallCollector {

    boolean activated;
    String modifier;

    public MethodModifierCollector(@NotNull Editor editor, @Nullable CESymbol symbol,
                                   String modifier, boolean activated) {
        super(editor, modifier + "methodmodifier", symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean isHintable(@NotNull PsiMethod element) {
        return element.getModifierList().hasModifierProperty(modifier);
    }
}
