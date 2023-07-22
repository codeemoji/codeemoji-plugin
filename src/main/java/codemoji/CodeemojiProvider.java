package codemoji;

import com.intellij.codeInsight.hints.*;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import lombok.Data;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ResourceBundle;

@Data
public abstract class CodeemojiProvider implements InlayHintsProvider<NoSettings> {

    ResourceBundle bundle = ResourceBundle.getBundle("CodeemojiBundle");

    @NotNull
    @Override
    public SettingsKey<NoSettings> getKey() {
        return new SettingsKey<>(getName().replaceAll(" ", ""));
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return bundle.getString(getBaseKey() + ".name");
    }

    @Nullable
    @Override
    public String getPreviewText() {
        return bundle.getString(getBaseKey() + ".preview").indent(2);
    }

    @NotNull
    @Override
    public ImmediateConfigurable createConfigurable(@NotNull NoSettings noSettings) {
        String description = bundle.getString(getBaseKey() + ".header");
        return new CodeemojiConfig(description);
    }

    @NotNull
    @Override
    public NoSettings createSettings() {
        return new NoSettings();
    }

    @Nullable
    @Override
    public InlayHintsCollector getCollectorFor(@NotNull PsiFile psiFile,
                                               @NotNull Editor editor,
                                               @NotNull NoSettings noSettings,
                                               @NotNull InlayHintsSink inlayHintsSink) {
        return getCollector(editor);
    }

    @Override
    public boolean isLanguageSupported(@NotNull Language language) {
        return "JAVA".equals(language.getID());
    }

    public void addInlayHint(@Nullable PsiMethod method, @NotNull InlayHintsSink sink, InlayPresentation inlay) {
        if ((method != null ? method.getNameIdentifier() : null) != null) {
            sink.addInlineElement(method.getNameIdentifier().getTextOffset() + method.getNameIdentifier().getTextLength(), false, inlay, false);
        }
    }

    public String getBaseKey() {
        return getClass().getSimpleName().toLowerCase();
    }

    public abstract InlayHintsCollector getCollector(Editor editor);

}