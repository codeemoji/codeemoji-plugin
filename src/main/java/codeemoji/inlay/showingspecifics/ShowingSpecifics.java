package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectClassCollector;
import codeemoji.core.collector.project.CEProjectMethodCollector;
import codeemoji.core.collector.project.CEProjectVariableCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.config.CERuleElement.*;

public class ShowingSpecifics extends CEProviderMulti<ShowingSpecificsSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        List<SharedBypassCollector> list = new ArrayList<>();

        String key = getKey();
        list.add(new CEProjectClassCollector(editor, key));
        list.add(new CEProjectMethodCollector(editor, key));
        list.add(new CEProjectVariableCollector(editor, key, FIELD));
        list.add(new CEProjectVariableCollector(editor, key, PARAMETER));
        list.add(new CEProjectVariableCollector(editor, key, LOCALVARIABLE));

        return list;
    }

    @Override
    public @NotNull CEConfigurableWindow<ShowingSpecificsSettings> createConfigurable() {
        return new ShowingSpecificsConfigurable();
    }

}








