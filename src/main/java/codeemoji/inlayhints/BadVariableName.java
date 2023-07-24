package codeemoji.inlayhints;

import codeemoji.core.CELocalVariableCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BadVariableName extends CEProvider<BadVariableNameSettings> {

    @Override
    public @Nullable String getPreviewText() {
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
    public InlayHintsCollector getCollector(@NotNull Editor editor) {
        return new CELocalVariableCollector(editor) {
            @Override
            public void processInlayHint(PsiElement element, InlayHintsSink sink) {
                if (getSettings().getNumberOfLetters() >= element.getTextLength()) {
                    InlayPresentation inlay = configureInlayHint(getName(), 0x1F90F);
                    addInlayHint(element, sink, inlay);
                }
            }
        };
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull BadVariableNameSettings settings) {
        return new BadVariableNameConfigurable(getHeader(), settings);
    }
}