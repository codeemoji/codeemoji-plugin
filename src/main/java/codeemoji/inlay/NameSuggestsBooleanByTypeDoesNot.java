package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codeemoji.core.CESymbol.CONFUSED;

public class NameSuggestsBooleanByTypeDoesNot extends CEProvider<NoSettings> {

    @Override
    public @Nullable String getPreviewText() {
        return """
                public class Customer {
                  private int isPattern;
                }""";
    }

    @Override
    public InlayHintsCollector getCollector(@NotNull Editor editor, @NotNull String keyId) {
        return new CEFieldCollector(editor, keyId) {
            @Override
            public void execute(@Nullable PsiField field, InlayHintsSink sink) {
                if (field != null) {
                    if (field.getName().startsWith("is") && !field.getType().equals(PsiTypes.booleanType())) {
                        addInlayHint(field.getNameIdentifier(), sink, CONFUSED);
                    }
                }
            }
        };
    }
}