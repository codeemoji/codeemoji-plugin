package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.base.simple.CESimpleVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

public class ShortDescriptiveName extends CEProvider<ShortDescriptiveNameSettings> {

    @Override
    protected void createCollectors(CEProvider<ShortDescriptiveNameSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.add(new CESimpleVariableCollector(editor, this) {
            @Override
            public boolean needsInlay(@NotNull PsiVariable element) {
                if (null != element.getNameIdentifier()) {
                    return getSettings().getNumberOfLetters() >= element.getNameIdentifier().getTextLength();
                }
                return false;
            }
        });
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<ShortDescriptiveNameSettings> createConfigurable() {
        return new ShortDescriptiveNameConfigurable();
    }
}