package codeemoji.inlay.invisiblefeatures;

import codeemoji.core.CEMultiProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.CEConstants.*;
import static com.intellij.psi.PsiModifier.*;

public class ShowingModifiers extends CEMultiProvider<ShowingModifiersSettings> {

    @Override
    public String getPreviewText() {
        return "";
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();
        list.add(new MethodModifierCollector(editor, GLOBE, PUBLIC, getSettings().isPublicMethod()));
        list.add(new MethodModifierCollector(editor, SHIELD, PROTECTED, getSettings().isProtectedMethod()));
        list.add(new MethodModifierCollector(editor, WHITE_FLAG, DEFAULT, getSettings().isDefaultMethod()));
        list.add(new MethodModifierCollector(editor, KEY, PRIVATE, getSettings().isPrivateMethod()));
        list.add(new MethodModifierCollector(editor, WHITE_CIRCLE, ABSTRACT, getSettings().isAbstractMethod()));
        list.add(new MethodModifierCollector(editor, SEMAPHORE, SYNCHRONIZED, getSettings().isSynchronizedMethod()));
        list.add(new MethodModifierCollector(editor, GEAR, NATIVE, getSettings().isNativeMethod()));
        list.add(new MethodModifierCollector(editor, HASHTAG, STRICTFP, getSettings().isStrictFPMethod()));
        //list.add(new MethodModifierCollector(editor, "defaultininterfacesmethodmodifier", ORANGE_CIRCLE, DEFAULT,getSettings().isDefaultInInterfacesMethod()));
        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingModifiersSettings settings) {
        return new ShowingModifiersConfigurable(settings);
    }
}








