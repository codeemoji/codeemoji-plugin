package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectClassCollector;
import codeemoji.core.collector.project.CEProjectMethodCollector;
import codeemoji.core.collector.project.CEProjectVariableCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.collector.config.CERuleElement.*;

@SuppressWarnings("UnstableApiUsage")
public class ShowingSpecifics extends CEProviderMulti<ShowingSpecificsSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();

        list.add(new CEProjectClassCollector(editor, getKeyId()));
        list.add(new CEProjectMethodCollector(editor, getKeyId()));
        list.add(new CEProjectVariableCollector(editor, FIELD, getKeyId()));
        list.add(new CEProjectVariableCollector(editor, PARAMETER, getKeyId()));
        list.add(new CEProjectVariableCollector(editor, LOCALVARIABLE, getKeyId()));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingSpecificsSettings settings) {
        return new ShowingSpecificsConfigurable(settings);
    }

}








