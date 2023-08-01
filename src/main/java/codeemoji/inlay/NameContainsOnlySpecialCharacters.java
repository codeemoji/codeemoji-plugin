package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CESymbol.CONFUSED;

public class NameContainsOnlySpecialCharacters extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  private int ___;
                  //...
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEFieldCollector(editor, getKeyId(), CONFUSED) {
            @Override
            public boolean putHintHere(@NotNull PsiField element) {
                return CEUtil.containsOnlySpecialCharacters(element.getName());
            }
        };
    }
}