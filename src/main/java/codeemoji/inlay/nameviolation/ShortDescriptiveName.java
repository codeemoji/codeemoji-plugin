package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CEVariableCollector(editor, getKey(), this::getSettings) {
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
    public @NotNull CEConfigurableWindow<ShortDescriptiveNameSettings> createConfigurable() {
        return new ShortDescriptiveNameConfigurable();
    }
}