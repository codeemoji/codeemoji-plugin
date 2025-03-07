package codeemoji.inlay.showingmodifiers;

import codeemoji.core.collector.simple.modifier.CEModifierClassCollector;
import codeemoji.core.collector.simple.modifier.CEModifierFieldCollector;
import codeemoji.core.collector.simple.modifier.CEModifierInterfaceMethodCollector;
import codeemoji.core.collector.simple.modifier.CEModifierMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.*;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.*;
import static com.intellij.psi.PsiModifier.*;

public class ShowingModifiers extends CEProviderMulti<ShowingModifiersSettings> {

    @Override
    public @NotNull List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        List<SharedBypassCollector> list = new ArrayList<>();
        String key = getKey();
        //class
        list.addAll(
                Arrays.asList(
                        new CEModifierClassCollector(editor, key, () -> getSettings().getPublic(), PUBLIC, getSettings().query(PUBLIC_CLASS)),
                        new CEModifierClassCollector(editor, key, () -> getSettings().getDefault(), DEFAULT, getSettings().query(DEFAULT_CLASS)),
                        new CEModifierClassCollector(editor, key, () -> getSettings().getFinal(), FINAL, getSettings().query(FINAL_CLASS)),
                        new CEModifierClassCollector(editor, key, () -> getSettings().getAbstract(), ABSTRACT, getSettings().query(ABSTRACT_CLASS))
                )
        );
        //fields
        list.addAll(
                Arrays.asList(
                        new CEModifierFieldCollector(editor, key, () -> getSettings().getPublic(), PUBLIC, getSettings().query(PUBLIC_FIELD)),
                        new CEModifierFieldCollector(editor, key, () -> getSettings().getDefault(), DEFAULT, getSettings().query(DEFAULT_FIELD)),
                        new CEModifierFieldCollector(editor, key, () -> getSettings().getFinal(), FINAL, getSettings().query(FINAL_FIELD)),
                        new CEModifierFieldCollector(editor, key, () -> getSettings().getProtected(), PROTECTED, getSettings().query(PROTECTED_FIELD)),
                        new CEModifierFieldCollector(editor, key, () -> getSettings().getPrivate(), PRIVATE, getSettings().query(PRIVATE_FIELD)),
                        new CEModifierFieldCollector(editor, key, () -> getSettings().getStatic(), STATIC, getSettings().query(STATIC_FIELD)),
                        new CEModifierFieldCollector(editor, key, () -> getSettings().getVolatile(), VOLATILE, getSettings().query(VOLATILE_FIELD)),
                        new CEModifierFieldCollector(editor, key, () -> getSettings().getTransient(), TRANSIENT, getSettings().query(TRANSIENT_FIELD))
                )
        );
        //methods
        list.addAll(
                Arrays.asList(
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getPublic(), PUBLIC, getSettings().query(PUBLIC_METHOD)),
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getDefault(), DEFAULT, getSettings().query(DEFAULT_METHOD)),
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getFinal(), FINAL, getSettings().query(FINAL_METHOD)),
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getProtected(), PROTECTED, getSettings().query(PROTECTED_METHOD)),
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getPrivate(), PRIVATE, getSettings().query(PRIVATE_METHOD)),
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getStatic(), STATIC, getSettings().query(STATIC_METHOD)),
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getAbstract(), ABSTRACT, getSettings().query(ABSTRACT_METHOD)),
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getSynchronized(), SYNCHRONIZED, getSettings().query(SYNCHRONIZED_METHOD)),
                        new CEModifierMethodCollector(editor, key, () -> getSettings().getNative(), NATIVE, getSettings().query(NATIVE_METHOD))
                )
        );
        list.add(new CEModifierInterfaceMethodCollector(editor, key, () -> getSettings().getDefaultInterface(), DEFAULT, getSettings().query(DEFAULT_INTERFACE_METHOD)));

        return list;
    }

    @Override
    public @NotNull CEConfigurableWindow<ShowingModifiersSettings> createConfigurable() {
        return new ShowingModifiersConfigurable();
    }

    public enum ScopeModifier {
        PUBLIC_CLASS, DEFAULT_CLASS, FINAL_CLASS, ABSTRACT_CLASS,
        PUBLIC_FIELD, DEFAULT_FIELD, FINAL_FIELD, PROTECTED_FIELD, PRIVATE_FIELD, STATIC_FIELD, VOLATILE_FIELD, TRANSIENT_FIELD,
        PUBLIC_METHOD, DEFAULT_METHOD, FINAL_METHOD, PROTECTED_METHOD, PRIVATE_METHOD, STATIC_METHOD, ABSTRACT_METHOD, SYNCHRONIZED_METHOD, NATIVE_METHOD, DEFAULT_INTERFACE_METHOD
    }
}








