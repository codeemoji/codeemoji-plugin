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

        //list.add(new ClassProjectCollector(editor));
        //list.add(new MethodProjectCollector(editor));
        //list.add(new FieldProjectCollector(editor));
        //list.add(new LocalVariableProjectCollector(editor));
        list.add(new ParameterProjectCollector(editor));

        return list;
    }

}








