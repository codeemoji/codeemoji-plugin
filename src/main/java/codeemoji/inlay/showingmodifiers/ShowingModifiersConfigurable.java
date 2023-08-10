package codeemoji.inlay.showingmodifiers;

import codeemoji.core.CEBundle;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.Modifier.*;
import static codeemoji.inlay.showingmodifiers.ShowingModifiersConstants.*;
import static com.intellij.psi.PsiModifier.*;

public record ShowingModifiersConfigurable(ShowingModifiersSettings settings) implements ImmediateConfigurable {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {

        var mainPanel = new JPanel();
        var classPanel = new JPanel();
        var fieldsPanel = new JPanel();
        var methodsPanel = new JPanel();

        classPanel.setLayout(new GridLayout(10, 1));
        fieldsPanel.setLayout(new GridLayout(10, 1));
        methodsPanel.setLayout(new GridLayout(10, 1));

        mainPanel.setBorder(BorderFactory.createTitledBorder(
                CEBundle.getString("inlay.showingmodifiers.options.title.modifiers")));
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 5, 2, 5);
        classPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,
                CEBundle.getString("inlay.showingmodifiers.options.title.classes")));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,
                CEBundle.getString("inlay.showingmodifiers.options.title.fields")));
        methodsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder,
                CEBundle.getString("inlay.showingmodifiers.options.title.methods")));

        //classes
        var publicClass = new JCheckBox(PUBLIC_SYMBOL_EMOJI + PUBLIC, settings().isPublicClass());
        var defaultClass = new JCheckBox(DEFAULT_SYMBOL_EMOJI + DEFAULT, settings().isDefaultClass());
        var finalClass = new JCheckBox(FINAL_SYMBOL_EMOJI + FINAL, settings().isFinalClass());
        var abstractClass = new JCheckBox(ABSTRACT_SYMBOL_EMOJI + ABSTRACT, settings().isAbstractClass());

        addChangeListener(publicClass, PUBLIC_CLASS, changeListener);
        addChangeListener(defaultClass, DEFAULT_CLASS, changeListener);
        addChangeListener(finalClass, FINAL_CLASS, changeListener);
        addChangeListener(abstractClass, ABSTRACT_CLASS, changeListener);

        classPanel.add(publicClass, BorderLayout.CENTER);
        classPanel.add(defaultClass, BorderLayout.CENTER);
        classPanel.add(finalClass, BorderLayout.CENTER);
        classPanel.add(abstractClass, BorderLayout.CENTER);

        //fields
        var publicField = new JCheckBox(PUBLIC_SYMBOL_EMOJI + PUBLIC, settings().isPublicField());
        var defaultField = new JCheckBox(DEFAULT_SYMBOL_EMOJI + DEFAULT, settings().isDefaultField());
        var finalField = new JCheckBox(FINAL_SYMBOL_EMOJI + FINAL, settings().isFinalField());
        var protectedField = new JCheckBox(PROTECTED_SYMBOL_EMOJI + PROTECTED, settings().isProtectedField());
        var privateField = new JCheckBox(PRIVATE_SYMBOL_EMOJI + PRIVATE, settings().isPrivateField());
        var staticField = new JCheckBox(STATIC_SYMBOL_EMOJI + STATIC, settings().isStaticField());
        var volatileField = new JCheckBox(VOLATILE_SYMBOL_EMOJI + VOLATILE, settings().isVolatileField());
        var transientField = new JCheckBox(TRANSIENT_SYMBOL_EMOJI + TRANSIENT, settings().isTransientField());

        addChangeListener(publicField, PUBLIC_FIELD, changeListener);
        addChangeListener(defaultField, DEFAULT_FIELD, changeListener);
        addChangeListener(finalField, FINAL_FIELD, changeListener);
        addChangeListener(protectedField, PROTECTED_FIELD, changeListener);
        addChangeListener(privateField, PRIVATE_FIELD, changeListener);
        addChangeListener(staticField, STATIC_FIELD, changeListener);
        addChangeListener(volatileField, VOLATILE_FIELD, changeListener);
        addChangeListener(transientField, TRANSIENT_FIELD, changeListener);

        fieldsPanel.add(publicField, BorderLayout.CENTER);
        fieldsPanel.add(defaultField, BorderLayout.CENTER);
        fieldsPanel.add(finalField, BorderLayout.CENTER);
        fieldsPanel.add(protectedField, BorderLayout.CENTER);
        fieldsPanel.add(privateField, BorderLayout.CENTER);
        fieldsPanel.add(staticField, BorderLayout.CENTER);
        fieldsPanel.add(volatileField, BorderLayout.CENTER);
        fieldsPanel.add(transientField, BorderLayout.CENTER);

        //methods
        var publicMethod = new JCheckBox(PUBLIC_SYMBOL_EMOJI + PUBLIC, settings().isPublicMethod());
        var defaultMethod = new JCheckBox(DEFAULT_SYMBOL_EMOJI + DEFAULT, settings().isDefaultMethod());
        var finalMethod = new JCheckBox(FINAL_SYMBOL_EMOJI + FINAL, settings().isPrivateMethod());
        var protectedMethod = new JCheckBox(PROTECTED_SYMBOL_EMOJI + PROTECTED, settings().isProtectedMethod());
        var privateMethod = new JCheckBox(PRIVATE_SYMBOL_EMOJI + PRIVATE, settings().isPrivateMethod());
        var staticMethod = new JCheckBox(STATIC_SYMBOL_EMOJI + STATIC, settings().isPrivateMethod());
        var abstractMethod = new JCheckBox(ABSTRACT_SYMBOL_EMOJI + ABSTRACT, settings().isAbstractMethod());
        var synchronizedMethod = new JCheckBox(SYNCHRONIZED_SYMBOL_EMOJI + SYNCHRONIZED, settings().isSynchronizedMethod());
        var nativeMethod = new JCheckBox(NATIVE_SYMBOL_EMOJI + NATIVE, settings().isNativeMethod());
        var defaultInterfaceMethod = new JCheckBox(DEFAULT_INTERFACE_SYMBOL_EMOJI + DEFAULT + " (in interfaces)", settings().isDefaultInterfaceMethod());

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

        methodsPanel.add(publicMethod, BorderLayout.CENTER);
        methodsPanel.add(defaultMethod, BorderLayout.CENTER);
        methodsPanel.add(finalMethod, BorderLayout.CENTER);
        methodsPanel.add(protectedMethod, BorderLayout.CENTER);
        methodsPanel.add(privateMethod, BorderLayout.CENTER);
        methodsPanel.add(staticMethod, BorderLayout.CENTER);
        methodsPanel.add(abstractMethod, BorderLayout.CENTER);
        methodsPanel.add(synchronizedMethod, BorderLayout.CENTER);
        methodsPanel.add(nativeMethod, BorderLayout.CENTER);
        methodsPanel.add(defaultInterfaceMethod, BorderLayout.CENTER);

        mainPanel.add(classPanel);
        mainPanel.add(fieldsPanel);
        mainPanel.add(methodsPanel);

        return FormBuilder.createFormBuilder()
                .addComponent(mainPanel)
                .getPanel();
    }

    private void addChangeListener(@NotNull JCheckBox checkBox, @NotNull ShowingModifiers.Modifier modifier, @NotNull ChangeListener changeListener) {
        checkBox.addChangeListener(event -> {
            settings.change(modifier, checkBox.isSelected());
            changeListener.settingsChanged();
        });
    }

}
