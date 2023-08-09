package codeemoji.inlay.invisiblefeatures;

import codeemoji.core.CEFieldReferenceCollector;
import codeemoji.core.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FieldModifierCollector extends CEFieldReferenceCollector {

    boolean activated;
    String modifier;

    public FieldModifierCollector(@NotNull Editor editor, @Nullable CESymbol symbol,
                                  String modifier, boolean activated) {
        super(editor, modifier + "fieldmodifier", symbol);
        this.activated = activated;
        this.modifier = modifier;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @Override
    public boolean isHintable(@NotNull PsiReferenceExpression element) {
        PsiReference reference = element.getReference();
        PsiElement resolveElement = reference.resolve();
        if (resolveElement instanceof PsiField field) {
            return field.getModifierList().hasModifierProperty(modifier);
        }
        return false;
    }
}
