package codeemoji.inlay;

import codeemoji.core.CEProvider;
import codeemoji.core.CEVariableCollector;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CEConstants.SMALL_NAME;

public class BadVariableName extends CEProvider<BadVariableNameSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  public String statement() {
                    String result = "";
                    while (rentals.hasMoreElements()) {
                      Rental a = (Rental) rentals.nextElement();
                      result += a.getMovie().getTitle() + ": "
                        + String.valueOf(a.calculateAmount()) +"\\n";
                    }
                    return result;
                  }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), SMALL_NAME) {
            @Override
            public boolean isHintable(@NotNull PsiVariable element) {
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