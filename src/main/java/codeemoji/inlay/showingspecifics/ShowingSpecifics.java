package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectClassCollector;
import codeemoji.core.collector.project.CEProjectMethodCollector;
import codeemoji.core.collector.project.CEProjectVariableCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.config.CERuleElement.*;

@SuppressWarnings("UnstableApiUsage")
public class ShowingSpecifics extends CEProviderMulti<ShowingSpecificsSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();

        SettingsKey<?> key = getKey();
        list.add(new CEProjectClassCollector(editor, key));
        list.add(new CEProjectMethodCollector(editor, key));
        list.add(new CEProjectVariableCollector(editor, key, FIELD));
        list.add(new CEProjectVariableCollector(editor, key, PARAMETER));
        list.add(new CEProjectVariableCollector(editor, key, LOCALVARIABLE));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingSpecificsSettings settings) {
        return new ShowingSpecificsConfigurable(settings);
    }

}








