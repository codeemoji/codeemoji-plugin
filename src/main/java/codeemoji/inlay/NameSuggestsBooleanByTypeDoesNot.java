package codeemoji.inlay;

import codeemoji.core.CEProvider;
import codeemoji.core.CEVariableCollector;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CESymbol.CONFUSED;

public class NameSuggestsBooleanByTypeDoesNot extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  private int isPattern;
                  //...
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), CONFUSED) {
            @Override
            public boolean isHintable(@NotNull PsiVariable element) {
                return element.getName().startsWith("is") && !element.getType().equals(PsiTypes.booleanType());
            }
        };
    }
}