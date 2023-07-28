package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codeemoji.core.CESymbol.CONFUSED;

public class NameContainsOnlySpecialCharacters extends CEProvider<NoSettings> {

    @Override
    public @Nullable String getPreviewText() {
        return """
                public class Customer {
                  private int isPattern;
                }""";
    }

    @Override
    public InlayHintsCollector getCollector(@NotNull Editor editor) {
        return new CEFieldCollector(editor) {
            @Override
            public void processInlay(@Nullable PsiField field, InlayHintsSink sink) {
                if (field != null) {
                    if (CEUtil.containsOnlySpecialCharacters(field.getName())) {
                        addInlay(field.getNameIdentifier(), sink, CONFUSED);
                    }
                }
            }
        };
    }
}