package codeemoji.inlay.showingmodifiers;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.showingspecifics.ShowingSpecificsSettings;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.*;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersSymbols.*;
import static com.intellij.psi.PsiModifier.*;

@SuppressWarnings({"DuplicatedCode"})
public class ShowingModifiersConfigurable extends CEConfigurableWindow<ShowingModifiersSettings> {

    @Override
    public @NotNull JComponent createComponent(ShowingModifiersSettings settings, Project project, Language language, ChangeListener changeListener) {
        var modifiersPanel = new JPanel();

        var classPanel = prepareClassPanel(settings, changeListener);
        var fieldPanel = prepareFieldPanel(settings, changeListener);
        var methodPanel = prepareMethodPanel(settings, changeListener);

        var title = CEBundle.getString("inlay.showingmodifiers.options.title.modifiers");
        modifiersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0), title));

        modifiersPanel.add(classPanel);
        modifiersPanel.add(fieldPanel);
        modifiersPanel.add(methodPanel);

        return FormBuilder.createFormBuilder()
                .addComponent(modifiersPanel)
                .getPanel();
    }

    private @NotNull JPanel prepareClassPanel(ShowingModifiersSettings settings, @NotNull ChangeListener changeListener) {
        var result = CEUtils.createBasicInnerPanel("inlay.showingmodifiers.options.title.classes", 10, 1);

        var publicClass = PUBLIC_SYMBOL.createCheckbox(PUBLIC, settings.query(PUBLIC_CLASS));
        var defaultClass = DEFAULT_SYMBOL.createCheckbox(DEFAULT, settings.query(DEFAULT_CLASS));
        var finalClass = FINAL_SYMBOL.createCheckbox(FINAL, settings.query(FINAL_CLASS));
        var abstractClass = ABSTRACT_SYMBOL.createCheckbox(ABSTRACT, settings.query(ABSTRACT_CLASS));

        addChangeListener(publicClass, PUBLIC_CLASS, settings, changeListener);
        addChangeListener(defaultClass, DEFAULT_CLASS, settings, changeListener);
        addChangeListener(finalClass, FINAL_CLASS, settings, changeListener);
        addChangeListener(abstractClass, ABSTRACT_CLASS, settings, changeListener);

        result.add(publicClass, BorderLayout.CENTER);
        result.add(defaultClass, BorderLayout.CENTER);
        result.add(finalClass, BorderLayout.CENTER);
        result.add(abstractClass, BorderLayout.CENTER);

        return result;
    }

    private @NotNull JPanel prepareFieldPanel(ShowingModifiersSettings settings, @NotNull ChangeListener changeListener) {
        var result = CEUtils.createBasicInnerPanel("inlay.showingmodifiers.options.title.fields", 10, 1);

        var publicField = PUBLIC_SYMBOL.createCheckbox(PUBLIC, settings.query(PUBLIC_FIELD));
        var defaultField = DEFAULT_SYMBOL.createCheckbox(DEFAULT, settings.query(DEFAULT_FIELD));
        var finalField = FINAL_VAR_SYMBOL.createCheckbox(FINAL, settings.query(FINAL_FIELD));
        var protectedField = PROTECTED_SYMBOL.createCheckbox(PROTECTED, settings.query(PROTECTED_FIELD));
        var privateField = PRIVATE_SYMBOL.createCheckbox(PRIVATE, settings.query(PRIVATE_FIELD));
        var staticField = STATIC_SYMBOL.createCheckbox(STATIC, settings.query(STATIC_FIELD));
        var volatileField = VOLATILE_SYMBOL.createCheckbox(VOLATILE, settings.query(VOLATILE_FIELD));
        var transientField = TRANSIENT_SYMBOL.createCheckbox(TRANSIENT, settings.query(TRANSIENT_FIELD));

        addChangeListener(publicField, PUBLIC_FIELD, settings, changeListener);
        addChangeListener(defaultField, DEFAULT_FIELD, settings, changeListener);
        addChangeListener(finalField, FINAL_FIELD, settings, changeListener);
        addChangeListener(protectedField, PROTECTED_FIELD, settings, changeListener);
        addChangeListener(privateField, PRIVATE_FIELD, settings, changeListener);
        addChangeListener(staticField, STATIC_FIELD, settings, changeListener);
        addChangeListener(volatileField, VOLATILE_FIELD, settings, changeListener);
        addChangeListener(transientField, TRANSIENT_FIELD, settings, changeListener);

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

    private @NotNull JPanel prepareMethodPanel(ShowingModifiersSettings settings, @NotNull ChangeListener changeListener) {
        var result = CEUtils.createBasicInnerPanel("inlay.showingmodifiers.options.title.methods", 10, 1);

        var publicMethod = PUBLIC_SYMBOL.createCheckbox(PUBLIC, settings.query(PUBLIC_METHOD));
        var defaultMethod = DEFAULT_SYMBOL.createCheckbox(DEFAULT, settings.query(DEFAULT_METHOD));
        var finalMethod = FINAL_SYMBOL.createCheckbox(FINAL, settings.query(FINAL_METHOD));
        var protectedMethod = PROTECTED_SYMBOL.createCheckbox(PROTECTED, settings.query(PROTECTED_METHOD));
        var privateMethod = PRIVATE_SYMBOL.createCheckbox(PRIVATE, settings.query(PRIVATE_METHOD));
        var staticMethod = STATIC_SYMBOL.createCheckbox(STATIC, settings.query(STATIC_METHOD));
        var abstractMethod = ABSTRACT_SYMBOL.createCheckbox(ABSTRACT, settings.query(ABSTRACT_METHOD));
        var synchronizedMethod = SYNCHRONIZED_SYMBOL.createCheckbox(SYNCHRONIZED, settings.query(SYNCHRONIZED_METHOD));
        var nativeMethod = NATIVE_SYMBOL.createCheckbox(NATIVE, settings.query(NATIVE_METHOD));
        var defaultInterfaceMethod = DEFAULT_INTERFACE_SYMBOL.createCheckbox(DEFAULT + " (in interfaces)", settings.query(DEFAULT_INTERFACE_METHOD));

        addChangeListener(publicMethod, PUBLIC_METHOD, settings, changeListener);
        addChangeListener(defaultMethod, DEFAULT_METHOD, settings, changeListener);
        addChangeListener(finalMethod, FINAL_METHOD, settings, changeListener);
        addChangeListener(protectedMethod, PROTECTED_METHOD, settings, changeListener);
        addChangeListener(privateMethod, PRIVATE_METHOD, settings, changeListener);
        addChangeListener(staticMethod, STATIC_METHOD, settings, changeListener);
        addChangeListener(abstractMethod, ABSTRACT_METHOD, settings, changeListener);
        addChangeListener(synchronizedMethod, SYNCHRONIZED_METHOD, settings, changeListener);
        addChangeListener(nativeMethod, NATIVE_METHOD, settings, changeListener);
        addChangeListener(defaultInterfaceMethod, DEFAULT_INTERFACE_METHOD, settings, changeListener);

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

    private void addChangeListener(@NotNull JCheckBox checkBox, @NotNull ShowingModifiers.ScopeModifier scopeModifier,
                                   ShowingModifiersSettings settings, @NotNull ChangeListener changeListener) {
        checkBox.addChangeListener(event -> {
            settings.update(scopeModifier, checkBox.isSelected());
            changeListener.settingsChanged();
        });
    }

}
