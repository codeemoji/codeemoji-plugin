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
        return null;
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();

        //class
        list.add(new ClassModifierCollector(editor, PUBLIC_SYM, PUBLIC, getSettings().isPublicClass()));
        list.add(new ClassModifierCollector(editor, ABSTRACT_SYM, ABSTRACT, getSettings().isAbstractClass()));
        list.add(new ClassModifierCollector(editor, FINAL_SYM, FINAL, getSettings().isFinalClass()));
        list.add(new ClassModifierCollector(editor, DEFAULT_SYM, DEFAULT, getSettings().isDefaultClass()));

        //fields
        list.add(new FieldModifierCollector(editor, PUBLIC_SYM, PUBLIC, getSettings().isPublicField()));
        list.add(new FieldModifierCollector(editor, PROTECTED_SYM, PROTECTED, getSettings().isProtectedField()));
        list.add(new FieldModifierCollector(editor, DEFAULT_SYM, DEFAULT, getSettings().isDefaultField()));
        list.add(new FieldModifierCollector(editor, PRIVATE_SYM, PRIVATE, getSettings().isPrivateField()));
        list.add(new FieldModifierCollector(editor, FINAL_SYM, FINAL, getSettings().isFinalField()));
        list.add(new FieldModifierCollector(editor, STATIC_SYM, STATIC, getSettings().isStaticField()));
        list.add(new FieldModifierCollector(editor, TRANSIENT_SYM, TRANSIENT, getSettings().isTransientField()));
        list.add(new FieldModifierCollector(editor, VOLATILE_SYM, VOLATILE, getSettings().isVolatileField()));
        list.add(new FieldModifierCollector(editor, NATIVE_SYM, NATIVE, getSettings().isNativeField()));

        //methods
        list.add(new MethodModifierCollector(editor, PUBLIC_SYM, PUBLIC, getSettings().isPublicMethod()));
        list.add(new MethodModifierCollector(editor, PROTECTED_SYM, PROTECTED, getSettings().isProtectedMethod()));
        list.add(new MethodModifierCollector(editor, DEFAULT_SYM, DEFAULT, getSettings().isDefaultMethod()));
        list.add(new MethodModifierCollector(editor, PRIVATE_SYM, PRIVATE, getSettings().isPrivateMethod()));
        list.add(new MethodModifierCollector(editor, STATIC_SYM, STATIC, getSettings().isStaticMethod()));
        list.add(new MethodModifierCollector(editor, FINAL_SYM, FINAL, getSettings().isFinalMethod()));
        list.add(new MethodModifierCollector(editor, ABSTRACT_SYM, ABSTRACT, getSettings().isAbstractMethod()));
        list.add(new MethodModifierCollector(editor, SYNCHRONIZED_SYM, SYNCHRONIZED, getSettings().isSynchronizedMethod()));
        list.add(new MethodModifierCollector(editor, NATIVE_SYM, NATIVE, getSettings().isNativeMethod()));
        list.add(new MethodInterfaceModifierCollector(editor, DEFAULT_INTERFACE_SYM, DEFAULT, getSettings().isDefaultInterfaceMethod()));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingModifiersSettings settings) {
        return new ShowingModifiersConfigurable(settings);
    }
}








