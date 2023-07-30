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
    public InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEFieldCollector(editor, getKey().getId()) {
            @Override
            public void processInlay(@Nullable PsiField field, InlayHintsSink sink) {
                if (field != null) {
                    if (field.getName().startsWith("is") && !field.getType().equals(PsiTypes.booleanType())) {
                        addInlay(field.getNameIdentifier(), sink, CONFUSED);
                    }
                }
            }
        };
    }
}