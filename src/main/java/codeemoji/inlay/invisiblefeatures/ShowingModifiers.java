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

        //class
        list.add(new ClassModifierCollector(editor, PUBLIC_SYMBOL, PUBLIC, getSettings().isPublicClass()));
        list.add(new ClassModifierCollector(editor, ABSTRACT_SYMBOL, ABSTRACT, getSettings().isAbstractClass()));
        list.add(new ClassModifierCollector(editor, FINAL_SYMBOL, FINAL, getSettings().isFinalClass()));
        list.add(new ClassModifierCollector(editor, STRICTFP_SYMBOL, STRICTFP, getSettings().isStrictFPClass()));
        list.add(new ClassModifierCollector(editor, DEFAULT_SYMBOL, DEFAULT, getSettings().isDefaultClass()));

        //fields
        list.add(new FieldModifierCollector(editor, PUBLIC_SYMBOL, PUBLIC, getSettings().isPublicField()));
        list.add(new FieldModifierCollector(editor, PROTECTED_SYMBOL, PROTECTED, getSettings().isProtectedField()));
        list.add(new FieldModifierCollector(editor, DEFAULT_SYMBOL, DEFAULT, getSettings().isDefaultField()));
        list.add(new FieldModifierCollector(editor, PRIVATE_SYMBOL, PRIVATE, getSettings().isPrivateField()));
        list.add(new FieldModifierCollector(editor, FINAL_SYMBOL, FINAL, getSettings().isFinalField()));
        list.add(new FieldModifierCollector(editor, STATIC_SYMBOL, STATIC, getSettings().isStaticField()));
        list.add(new FieldModifierCollector(editor, TRANSIENT_SYMBOL, TRANSIENT, getSettings().isTransientField()));
        list.add(new FieldModifierCollector(editor, VOLATILE_SYMBOL, VOLATILE, getSettings().isVolatileField()));
        list.add(new FieldModifierCollector(editor, NATIVE_SYMBOL, NATIVE, getSettings().isNativeField()));

        //methods
        list.add(new MethodModifierCollector(editor, PUBLIC_SYMBOL, PUBLIC, getSettings().isPublicMethod()));
        list.add(new MethodModifierCollector(editor, PROTECTED_SYMBOL, PROTECTED, getSettings().isProtectedMethod()));
        list.add(new MethodModifierCollector(editor, DEFAULT_SYMBOL, DEFAULT, getSettings().isDefaultMethod()));
        list.add(new MethodModifierCollector(editor, PRIVATE_SYMBOL, PRIVATE, getSettings().isPrivateMethod()));
        list.add(new MethodModifierCollector(editor, ABSTRACT_SYMBOL, ABSTRACT, getSettings().isAbstractMethod()));
        list.add(new MethodModifierCollector(editor, SYNCHRONIZED_SYMBOL, SYNCHRONIZED, getSettings().isSynchronizedMethod()));
        list.add(new MethodModifierCollector(editor, NATIVE_SYMBOL, NATIVE, getSettings().isNativeMethod()));
        list.add(new MethodModifierCollector(editor, STRICTFP_SYMBOL, STRICTFP, getSettings().isStrictFPMethod()));
        //list.add(new MethodModifierCollector(editor, "defaultininterfacesmethodmodifier", ORANGE_CIRCLE, DEFAULT,getSettings().isDefaultInInterfacesMethod()));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingModifiersSettings settings) {
        return new ShowingModifiersConfigurable(settings);
    }
}








