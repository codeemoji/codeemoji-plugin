package codeemoji.inlay.showingmodifiers;

import codeemoji.core.CEMultiProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.Modifier.*;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersConstants.*;
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
        list.add(new ClassModifierCollector(editor, PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_CLASS)));
        list.add(new ClassModifierCollector(editor, DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_CLASS)));
        list.add(new ClassModifierCollector(editor, FINAL_SYMBOL, FINAL, getSettings().query(FINAL_CLASS)));
        list.add(new ClassModifierCollector(editor, ABSTRACT_SYMBOL, ABSTRACT, getSettings().query(ABSTRACT_CLASS)));
        //fields
        list.add(new FieldModifierCollector(editor, PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_FIELD)));
        list.add(new FieldModifierCollector(editor, DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_FIELD)));
        list.add(new FieldModifierCollector(editor, FINAL_VAR_SYMBOL, FINAL, getSettings().query(FINAL_FIELD)));
        list.add(new FieldModifierCollector(editor, PROTECTED_SYMBOL, PROTECTED, getSettings().query(PROTECTED_FIELD)));
        list.add(new FieldModifierCollector(editor, PRIVATE_SYMBOL, PRIVATE, getSettings().query(PRIVATE_FIELD)));
        list.add(new FieldModifierCollector(editor, STATIC_SYMBOL, STATIC, getSettings().query(STATIC_FIELD)));
        list.add(new FieldModifierCollector(editor, VOLATILE_SYMBOL, VOLATILE, getSettings().query(VOLATILE_FIELD)));
        list.add(new FieldModifierCollector(editor, TRANSIENT_SYMBOL, TRANSIENT, getSettings().query(TRANSIENT_FIELD)));
        //methods
        list.add(new MethodModifierCollector(editor, PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_METHOD)));
        list.add(new MethodModifierCollector(editor, DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_METHOD)));
        list.add(new MethodModifierCollector(editor, FINAL_SYMBOL, FINAL, getSettings().query(FINAL_METHOD)));
        list.add(new MethodModifierCollector(editor, PROTECTED_SYMBOL, PROTECTED, getSettings().query(PROTECTED_METHOD)));
        list.add(new MethodModifierCollector(editor, PRIVATE_SYMBOL, PRIVATE, getSettings().query(PRIVATE_METHOD)));
        list.add(new MethodModifierCollector(editor, STATIC_SYMBOL, STATIC, getSettings().query(STATIC_METHOD)));
        list.add(new MethodModifierCollector(editor, ABSTRACT_SYMBOL, ABSTRACT, getSettings().query(ABSTRACT_METHOD)));
        list.add(new MethodModifierCollector(editor, SYNCHRONIZED_SYMBOL, SYNCHRONIZED, getSettings().query(SYNCHRONIZED_METHOD)));
        list.add(new MethodModifierCollector(editor, NATIVE_SYMBOL, NATIVE, getSettings().query(NATIVE_METHOD)));
        list.add(new MethodInterfaceModifierCollector(editor, DEFAULT_INTERFACE_SYMBOL, DEFAULT, getSettings().query(DEFAULT_INTERFACE_METHOD)));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingModifiersSettings settings) {
        return new ShowingModifiersConfigurable(settings);
    }

    public enum Modifier implements Serializable {
        PUBLIC_CLASS, DEFAULT_CLASS, FINAL_CLASS, ABSTRACT_CLASS,
        PUBLIC_FIELD, DEFAULT_FIELD, FINAL_FIELD, PROTECTED_FIELD, PRIVATE_FIELD, STATIC_FIELD, VOLATILE_FIELD, TRANSIENT_FIELD,
        PUBLIC_METHOD, DEFAULT_METHOD, FINAL_METHOD, PROTECTED_METHOD, PRIVATE_METHOD, STATIC_METHOD, ABSTRACT_METHOD, SYNCHRONIZED_METHOD, NATIVE_METHOD, DEFAULT_INTERFACE_METHOD
    }
}








