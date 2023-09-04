package codeemoji.inlay.showingmodifiers;

import codeemoji.core.provider.CEMultiProvider;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.*;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.*;
import static com.intellij.psi.PsiModifier.*;

@SuppressWarnings("UnstableApiUsage")
public class ShowingModifiers extends CEMultiProvider<ShowingModifiersSettings> {

    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();
        //class
        list.addAll(
                Arrays.asList(
                        new ModifierClassCollector(editor, getKeyId(), PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_CLASS)),
                        new ModifierClassCollector(editor, getKeyId(), DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_CLASS)),
                        new ModifierClassCollector(editor, getKeyId(), FINAL_SYMBOL, FINAL, getSettings().query(FINAL_CLASS)),
                        new ModifierClassCollector(editor, getKeyId(), ABSTRACT_SYMBOL, ABSTRACT, getSettings().query(ABSTRACT_CLASS))
                )
        );
        //fields
        list.addAll(
                Arrays.asList(
                        new ModifierFieldCollector(editor, getKeyId(), PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_FIELD)),
                        new ModifierFieldCollector(editor, getKeyId(), DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_FIELD)),
                        new ModifierFieldCollector(editor, getKeyId(), FINAL_VAR_SYMBOL, FINAL, getSettings().query(FINAL_FIELD)),
                        new ModifierFieldCollector(editor, getKeyId(), PROTECTED_SYMBOL, PROTECTED, getSettings().query(PROTECTED_FIELD)),
                        new ModifierFieldCollector(editor, getKeyId(), PRIVATE_SYMBOL, PRIVATE, getSettings().query(PRIVATE_FIELD)),
                        new ModifierFieldCollector(editor, getKeyId(), STATIC_SYMBOL, STATIC, getSettings().query(STATIC_FIELD)),
                        new ModifierFieldCollector(editor, getKeyId(), VOLATILE_SYMBOL, VOLATILE, getSettings().query(VOLATILE_FIELD)),
                        new ModifierFieldCollector(editor, getKeyId(), TRANSIENT_SYMBOL, TRANSIENT, getSettings().query(TRANSIENT_FIELD))
                )
        );
        //methods
        list.addAll(
                Arrays.asList(
                        new ModifierMethodCollector(editor, getKeyId(), PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_METHOD)),
                        new ModifierMethodCollector(editor, getKeyId(), DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_METHOD)),
                        new ModifierMethodCollector(editor, getKeyId(), FINAL_SYMBOL, FINAL, getSettings().query(FINAL_METHOD)),
                        new ModifierMethodCollector(editor, getKeyId(), PROTECTED_SYMBOL, PROTECTED, getSettings().query(PROTECTED_METHOD)),
                        new ModifierMethodCollector(editor, getKeyId(), PRIVATE_SYMBOL, PRIVATE, getSettings().query(PRIVATE_METHOD)),
                        new ModifierMethodCollector(editor, getKeyId(), STATIC_SYMBOL, STATIC, getSettings().query(STATIC_METHOD)),
                        new ModifierMethodCollector(editor, getKeyId(), ABSTRACT_SYMBOL, ABSTRACT, getSettings().query(ABSTRACT_METHOD)),
                        new ModifierMethodCollector(editor, getKeyId(), SYNCHRONIZED_SYMBOL, SYNCHRONIZED, getSettings().query(SYNCHRONIZED_METHOD)),
                        new ModifierMethodCollector(editor, getKeyId(), NATIVE_SYMBOL, NATIVE, getSettings().query(NATIVE_METHOD))
                )
        );
        list.add(new ModifierInterfaceMethodCollector(editor, getKeyId(), DEFAULT_INTERFACE_SYMBOL, DEFAULT, getSettings().query(DEFAULT_INTERFACE_METHOD)));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingModifiersSettings settings) {
        return new ShowingModifiersConfigurable(settings);
    }

    public enum ScopeModifier implements Serializable {
        PUBLIC_CLASS, DEFAULT_CLASS, FINAL_CLASS, ABSTRACT_CLASS,
        PUBLIC_FIELD, DEFAULT_FIELD, FINAL_FIELD, PROTECTED_FIELD, PRIVATE_FIELD, STATIC_FIELD, VOLATILE_FIELD, TRANSIENT_FIELD,
        PUBLIC_METHOD, DEFAULT_METHOD, FINAL_METHOD, PROTECTED_METHOD, PRIVATE_METHOD, STATIC_METHOD, ABSTRACT_METHOD, SYNCHRONIZED_METHOD, NATIVE_METHOD, DEFAULT_INTERFACE_METHOD
    }
}








