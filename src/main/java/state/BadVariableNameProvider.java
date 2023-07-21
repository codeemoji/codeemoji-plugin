package state;

import com.intellij.codeInsight.hints.*;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BadVariableNameProvider implements InlayHintsProvider<BadVariableNameState> {

    @Nullable
    @Override
    public InlayHintsCollector getCollectorFor(@NotNull PsiFile file,
                                               @NotNull Editor editor,
                                               @NotNull BadVariableNameState settingsState,
                                               @NotNull InlayHintsSink inlayHintsSink) {
        return new BadVariableNameCollector(editor, settingsState);
    }

    @NotNull
    @Override
    public BadVariableNameState createSettings() {
        return BadVariableNameState.getInstance();
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return BadVariableNameConfig.NAME;
    }

    @NotNull
    @Override
    public SettingsKey<BadVariableNameState> getKey() {
        return new SettingsKey<>(BadVariableNameConfig.KEY);
    }

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  //...
                  public String statement() {
                    //...
                    while (rentals.hasMoreElements()) {
                      Rental a = (Rental) rentals.nextElement();
                      result += a.getMovie().getTitle() + ": "
                        + String.valueOf(a.calculateAmount());
                    }
                    //...
                    return result;
                  }
                }""".trim().indent(2);
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull BadVariableNameState settings) {
        return new BadVariableNameConfig(settings);
    }

    @Override
    public boolean isLanguageSupported(@NotNull Language language) {
        return "JAVA".equals(language.getID());
    }

    @Override
    public boolean isVisibleInSettings() {
        return true;
    }
}