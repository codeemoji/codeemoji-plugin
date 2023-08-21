package codeemoji.inlay.showingspecifics;

import codeemoji.core.provider.CEMultiProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShowingSpecifics extends CEMultiProvider<ShowingSpecificsSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();

        list.add(new ProjectClassCollector(editor, getKeyId()));
        list.add(new ProjectMethodCollector(editor, getKeyId()));
        list.add(new ProjectFieldCollector(editor, getKeyId()));
        list.add(new ProjectLocalVariableCollector(editor, getKeyId()));
        list.add(new ProjectParameterCollector(editor, getKeyId()));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingSpecificsSettings settings) {
        return new ShowingSpecificsConfigurable(settings);
    }

}








