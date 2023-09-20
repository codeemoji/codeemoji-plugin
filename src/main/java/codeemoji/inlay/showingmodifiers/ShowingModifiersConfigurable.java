package codeemoji.inlay.showingmodifiers;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

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

@SuppressWarnings({"UnstableApiUsage", "DuplicatedCode"})
record ShowingModifiersConfigurable(ShowingModifiersSettings settings) implements ImmediateConfigurable {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
        var modifiersPanel = new JPanel();

        var classPanel = prepareClassPanel(changeListener);
        var fieldPanel = prepareFieldPanel(changeListener);
        var methodPanel = prepareMethodPanel(changeListener);

        var title = CEBundle.getString("inlay.showingmodifiers.options.title.modifiers");
        modifiersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0), title));

        modifiersPanel.add(classPanel);
        modifiersPanel.add(fieldPanel);
        modifiersPanel.add(methodPanel);

        return FormBuilder.createFormBuilder()
                .addComponent(modifiersPanel)
                .getPanel();
    }

    private @NotNull JPanel prepareClassPanel(@NotNull ChangeListener changeListener) {
        var result = CEUtils.createBasicInnerPanel("inlay.showingmodifiers.options.title.classes", 10, 1);

        var publicClass = new JCheckBox(PUBLIC_SYMBOL.getEmoji() + PUBLIC, settings().query(PUBLIC_CLASS));
        var defaultClass = new JCheckBox(DEFAULT_SYMBOL.getEmoji() + DEFAULT, settings().query(DEFAULT_CLASS));
        var finalClass = new JCheckBox(FINAL_SYMBOL.getEmoji() + FINAL, settings().query(FINAL_CLASS));
        var abstractClass = new JCheckBox(ABSTRACT_SYMBOL.getEmoji() + ABSTRACT, settings().query(ABSTRACT_CLASS));

        addChangeListener(publicClass, PUBLIC_CLASS, changeListener);
        addChangeListener(defaultClass, DEFAULT_CLASS, changeListener);
        addChangeListener(finalClass, FINAL_CLASS, changeListener);
        addChangeListener(abstractClass, ABSTRACT_CLASS, changeListener);

        result.add(publicClass, BorderLayout.CENTER);
        result.add(defaultClass, BorderLayout.CENTER);
        result.add(finalClass, BorderLayout.CENTER);
        result.add(abstractClass, BorderLayout.CENTER);

        return result;
    }

    private @NotNull JPanel prepareFieldPanel(@NotNull ChangeListener changeListener) {
        var result = CEUtils.createBasicInnerPanel("inlay.showingmodifiers.options.title.fields", 10, 1);

        var publicField = new JCheckBox(PUBLIC_SYMBOL.getEmoji() + PUBLIC, settings().query(PUBLIC_FIELD));
        var defaultField = new JCheckBox(DEFAULT_SYMBOL.getEmoji() + DEFAULT, settings().query(DEFAULT_FIELD));
        var finalField = new JCheckBox(FINAL_VAR_SYMBOL.getEmoji() + FINAL, settings().query(FINAL_FIELD));
        var protectedField = new JCheckBox(PROTECTED_SYMBOL.getEmoji() + PROTECTED, settings().query(PROTECTED_FIELD));
        var privateField = new JCheckBox(PRIVATE_SYMBOL.getEmoji() + PRIVATE, settings().query(PRIVATE_FIELD));
        var staticField = new JCheckBox(STATIC_SYMBOL.getEmoji() + STATIC, settings().query(STATIC_FIELD));
        var volatileField = new JCheckBox(VOLATILE_SYMBOL.getEmoji() + VOLATILE, settings().query(VOLATILE_FIELD));
        var transientField = new JCheckBox(TRANSIENT_SYMBOL.getEmoji() + TRANSIENT, settings().query(TRANSIENT_FIELD));

        addChangeListener(publicField, PUBLIC_FIELD, changeListener);
        addChangeListener(defaultField, DEFAULT_FIELD, changeListener);
        addChangeListener(finalField, FINAL_FIELD, changeListener);
        addChangeListener(protectedField, PROTECTED_FIELD, changeListener);
        addChangeListener(privateField, PRIVATE_FIELD, changeListener);
        addChangeListener(staticField, STATIC_FIELD, changeListener);
        addChangeListener(volatileField, VOLATILE_FIELD, changeListener);
        addChangeListener(transientField, TRANSIENT_FIELD, changeListener);

        result.add(publicField, BorderLayout.CENTER);
        result.add(defaultField, BorderLayout.CENTER);
        result.add(finalField, BorderLayout.CENTER);
        result.add(protectedField, BorderLayout.CENTER);
        result.add(privateField, BorderLayout.CENTER);
        result.add(staticField, BorderLayout.CENTER);
        result.add(volatileField, BorderLayout.CENTER);
        result.add(transientField, BorderLayout.CENTER);

        return result;
    }

    private @NotNull JPanel prepareMethodPanel(@NotNull ChangeListener changeListener) {
        var result = CEUtils.createBasicInnerPanel("inlay.showingmodifiers.options.title.methods", 10, 1);

        var publicMethod = new JCheckBox(PUBLIC_SYMBOL.getEmoji() + PUBLIC, settings().query(PUBLIC_METHOD));
        var defaultMethod = new JCheckBox(DEFAULT_SYMBOL.getEmoji() + DEFAULT, settings().query(DEFAULT_METHOD));
        var finalMethod = new JCheckBox(FINAL_SYMBOL.getEmoji() + FINAL, settings().query(FINAL_METHOD));
        var protectedMethod = new JCheckBox(PROTECTED_SYMBOL.getEmoji() + PROTECTED, settings().query(PROTECTED_METHOD));
        var privateMethod = new JCheckBox(PRIVATE_SYMBOL.getEmoji() + PRIVATE, settings().query(PRIVATE_METHOD));
        var staticMethod = new JCheckBox(STATIC_SYMBOL.getEmoji() + STATIC, settings().query(STATIC_METHOD));
        var abstractMethod = new JCheckBox(ABSTRACT_SYMBOL.getEmoji() + ABSTRACT, settings().query(ABSTRACT_METHOD));
        var synchronizedMethod = new JCheckBox(SYNCHRONIZED_SYMBOL.getEmoji() + SYNCHRONIZED, settings().query(SYNCHRONIZED_METHOD));
        var nativeMethod = new JCheckBox(NATIVE_SYMBOL.getEmoji() + NATIVE, settings().query(NATIVE_METHOD));
        var defaultInterfaceMethod = new JCheckBox(DEFAULT_INTERFACE_SYMBOL.getEmoji() + DEFAULT + " (in interfaces)", settings().query(DEFAULT_INTERFACE_METHOD));

        addChangeListener(publicMethod, PUBLIC_METHOD, changeListener);
        addChangeListener(defaultMethod, DEFAULT_METHOD, changeListener);
        addChangeListener(finalMethod, FINAL_METHOD, changeListener);
        addChangeListener(protectedMethod, PROTECTED_METHOD, changeListener);
        addChangeListener(privateMethod, PRIVATE_METHOD, changeListener);
        addChangeListener(staticMethod, STATIC_METHOD, changeListener);
        addChangeListener(abstractMethod, ABSTRACT_METHOD, changeListener);
        addChangeListener(synchronizedMethod, SYNCHRONIZED_METHOD, changeListener);
        addChangeListener(nativeMethod, NATIVE_METHOD, changeListener);
        addChangeListener(defaultInterfaceMethod, DEFAULT_INTERFACE_METHOD, changeListener);

        result.add(publicMethod, BorderLayout.CENTER);
        result.add(defaultMethod, BorderLayout.CENTER);
        result.add(finalMethod, BorderLayout.CENTER);
        result.add(protectedMethod, BorderLayout.CENTER);
        result.add(privateMethod, BorderLayout.CENTER);
        result.add(staticMethod, BorderLayout.CENTER);
        result.add(abstractMethod, BorderLayout.CENTER);
        result.add(synchronizedMethod, BorderLayout.CENTER);
        result.add(nativeMethod, BorderLayout.CENTER);
        result.add(defaultInterfaceMethod, BorderLayout.CENTER);

        return result;
    }

    private void addChangeListener(@NotNull JCheckBox checkBox, @NotNull ShowingModifiers.ScopeModifier scopeModifier, @NotNull ChangeListener changeListener) {
        checkBox.addChangeListener(event -> {
            settings().update(scopeModifier, checkBox.isSelected());
            changeListener.settingsChanged();
        });
    }

}
