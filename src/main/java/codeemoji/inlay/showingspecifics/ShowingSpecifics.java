package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectClassCollector;
import codeemoji.core.collector.project.CEProjectMethodCollector;
import codeemoji.core.collector.project.CEProjectVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.config.CERuleElement.*;

public class ShowingSpecifics extends CEProvider<ShowingSpecificsSettings> {

    @Override
    protected void createCollectors(CEProvider<ShowingSpecificsSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        String key = getKey();
        builder.add(new CEProjectClassCollector(editor, key));
        builder.add(new CEProjectMethodCollector(editor, key));
        builder.add(new CEProjectVariableCollector(editor, key, FIELD));
        builder.add(new CEProjectVariableCollector(editor, key, PARAMETER));
        builder.add(new CEProjectVariableCollector(editor, key, LOCALVARIABLE));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<ShowingSpecificsSettings> createConfigurable() {
        return new ShowingSpecificsConfigurable();
    }

}








