package codeemoji.inlay;

import codeemoji.core.CELocalVariableCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiLocalVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CESymbol.SMALL_NAME;

public class BadVariableName extends CEProvider<BadVariableNameSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  public String statement() {
                    while (rentals.hasMoreElements()) {
                      Rental a = (Rental) rentals.nextElement();
                      result += a.getMovie().getTitle() + ": "
                        + String.valueOf(a.calculateAmount());
                    }
                    return result;
                  }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CELocalVariableCollector(editor, getKeyId(), SMALL_NAME) {
            @Override
            public boolean putHintHere(@NotNull PsiLocalVariable element) {
                if (element.getNameIdentifier() != null) {
                    return getSettings().getNumberOfLetters() >= element.getNameIdentifier().getTextLength();
                }
                return false;
            }
        };
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull BadVariableNameSettings settings) {
        return new BadVariableNameConfigurable(settings);
    }
}