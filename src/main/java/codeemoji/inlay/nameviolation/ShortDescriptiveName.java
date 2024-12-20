package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEVariableCollector(editor, getKey(), SMALL_NAME) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element){
                if (null != element.getNameIdentifier()) {
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