package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

public class ShortDescriptiveName extends CEProvider<ShortDescriptiveNameSettings> {

    @Override
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleVariableCollector(editor, getKey(), mainSymbol()) {
            @Override
            public boolean needsInlay(@NotNull PsiVariable element){
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