package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.simple.modifier.CEModifierClassCollector;
import codeemoji.core.collector.simple.modifier.CEModifierFieldCollector;
import codeemoji.core.collector.simple.modifier.CEModifierInterfaceMethodCollector;
import codeemoji.core.collector.simple.modifier.CEModifierMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.*;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.ABSTRACT_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.DEFAULT_INTERFACE_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.DEFAULT_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.FINAL_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.FINAL_VAR_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.NATIVE_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.PRIVATE_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.PROTECTED_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.PUBLIC_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.STATIC_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.SYNCHRONIZED_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.TRANSIENT_SYMBOL;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.VOLATILE_SYMBOL;
import static com.intellij.psi.PsiModifier.ABSTRACT;
import static com.intellij.psi.PsiModifier.DEFAULT;
import static com.intellij.psi.PsiModifier.FINAL;
import static com.intellij.psi.PsiModifier.NATIVE;
import static com.intellij.psi.PsiModifier.PRIVATE;
import static com.intellij.psi.PsiModifier.PROTECTED;
import static com.intellij.psi.PsiModifier.PUBLIC;
import static com.intellij.psi.PsiModifier.STATIC;
import static com.intellij.psi.PsiModifier.SYNCHRONIZED;
import static com.intellij.psi.PsiModifier.TRANSIENT;
import static com.intellij.psi.PsiModifier.VOLATILE;

@SuppressWarnings("UnstableApiUsage")
public class ShowingModifiers extends CEProviderMulti<ShowingModifiersSettings> {

    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        List<InlayHintsCollector> list = new ArrayList<>();
        SettingsKey<?> key = getKey();
        //class
        list.addAll(
                Arrays.asList(
                        new CEModifierClassCollector(editor, key, PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_CLASS)),
                        new CEModifierClassCollector(editor, key, DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_CLASS)),
                        new CEModifierClassCollector(editor, key, FINAL_SYMBOL, FINAL, getSettings().query(FINAL_CLASS)),
                        new CEModifierClassCollector(editor, key, ABSTRACT_SYMBOL, ABSTRACT, getSettings().query(ABSTRACT_CLASS))
                )
        );
        //fields
        list.addAll(
                Arrays.asList(
                        new CEModifierFieldCollector(editor, key, PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_FIELD)),
                        new CEModifierFieldCollector(editor, key, DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_FIELD)),
                        new CEModifierFieldCollector(editor, key, FINAL_VAR_SYMBOL, FINAL, getSettings().query(FINAL_FIELD)),
                        new CEModifierFieldCollector(editor, key, PROTECTED_SYMBOL, PROTECTED, getSettings().query(PROTECTED_FIELD)),
                        new CEModifierFieldCollector(editor, key, PRIVATE_SYMBOL, PRIVATE, getSettings().query(PRIVATE_FIELD)),
                        new CEModifierFieldCollector(editor, key, STATIC_SYMBOL, STATIC, getSettings().query(STATIC_FIELD)),
                        new CEModifierFieldCollector(editor, key, VOLATILE_SYMBOL, VOLATILE, getSettings().query(VOLATILE_FIELD)),
                        new CEModifierFieldCollector(editor, key, TRANSIENT_SYMBOL, TRANSIENT, getSettings().query(TRANSIENT_FIELD))
                )
        );
        //methods
        list.addAll(
                Arrays.asList(
                        new CEModifierMethodCollector(editor, key, PUBLIC_SYMBOL, PUBLIC, getSettings().query(PUBLIC_METHOD)),
                        new CEModifierMethodCollector(editor, key, DEFAULT_SYMBOL, DEFAULT, getSettings().query(DEFAULT_METHOD)),
                        new CEModifierMethodCollector(editor, key, FINAL_SYMBOL, FINAL, getSettings().query(FINAL_METHOD)),
                        new CEModifierMethodCollector(editor, key, PROTECTED_SYMBOL, PROTECTED, getSettings().query(PROTECTED_METHOD)),
                        new CEModifierMethodCollector(editor, key, PRIVATE_SYMBOL, PRIVATE, getSettings().query(PRIVATE_METHOD)),
                        new CEModifierMethodCollector(editor, key, STATIC_SYMBOL, STATIC, getSettings().query(STATIC_METHOD)),
                        new CEModifierMethodCollector(editor, key, ABSTRACT_SYMBOL, ABSTRACT, getSettings().query(ABSTRACT_METHOD)),
                        new CEModifierMethodCollector(editor, key, SYNCHRONIZED_SYMBOL, SYNCHRONIZED, getSettings().query(SYNCHRONIZED_METHOD)),
                        new CEModifierMethodCollector(editor, key, NATIVE_SYMBOL, NATIVE, getSettings().query(NATIVE_METHOD))
                )
        );
        list.add(new CEModifierInterfaceMethodCollector(editor, key, DEFAULT_INTERFACE_SYMBOL, DEFAULT, getSettings().query(DEFAULT_INTERFACE_METHOD)));

        return list;
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull ShowingModifiersSettings settings) {
        return new ShowingModifiersConfigurable(settings);
    }

    public enum ScopeModifier {
        PUBLIC_CLASS, DEFAULT_CLASS, FINAL_CLASS, ABSTRACT_CLASS,
        PUBLIC_FIELD, DEFAULT_FIELD, FINAL_FIELD, PROTECTED_FIELD, PRIVATE_FIELD, STATIC_FIELD, VOLATILE_FIELD, TRANSIENT_FIELD,
        PUBLIC_METHOD, DEFAULT_METHOD, FINAL_METHOD, PROTECTED_METHOD, PRIVATE_METHOD, STATIC_METHOD, ABSTRACT_METHOD, SYNCHRONIZED_METHOD, NATIVE_METHOD, DEFAULT_INTERFACE_METHOD
    }
}








