package codeemoji.inlay.showingspecifics;

import codeemoji.core.provider.CEMultiProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShowingSpecifics extends CEMultiProvider<NoSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();

        list.add(new ProjectClassCollector(editor));
        list.add(new ProjectMethodCollector(editor));
        list.add(new ProjectFieldCollector(editor));
        list.add(new ProjectLocalVariableCollector(editor));
        list.add(new ProjectParameterCollector(editor));

        return list;
    }

}








