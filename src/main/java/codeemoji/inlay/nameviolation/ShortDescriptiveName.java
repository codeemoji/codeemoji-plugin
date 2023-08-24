package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.basic.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.SMALL_NAME;

@SuppressWarnings("UnstableApiUsage")
public class ShortDescriptiveName extends CEProvider<ShortDescriptiveNameSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                                
                  private String s = ": ";
                                
                  public String statement(String p) {
                    String result = p + "-> ";
                    while (rentals.hasMoreElements()) {
                      Rental a = (Rental) rentals.nextElement();
                      result += a.getMovie().getTitle() + s
                        + String.valueOf(a.calculateAmount());
                    }
                    return result;
                  }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), SMALL_NAME) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element) {
                if (element.getNameIdentifier() != null) {
                    return getSettings().getNumberOfLetters() >= element.getNameIdentifier().getTextLength();
                }
                return false;
            }
        };
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShortDescriptiveNameSettings settings) {
        return new ShortDescriptiveNameConfigurable(settings);
    }
}