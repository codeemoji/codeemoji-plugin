package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEClassProjectCollector;
import codeemoji.core.collector.project.CEMethodProjectCollector;
import codeemoji.core.collector.project.CEVariableProjectCollector;
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

        list.add(new CEClassProjectCollector(editor));
        list.add(new CEMethodProjectCollector(editor));
        list.add(new CEVariableProjectCollector(editor));

        return list;
    }

}








