package codeemoji.inlay.invisiblefeatures;

import codeemoji.core.CEModifier;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import java.awt.*;

import static codeemoji.core.CEConstants.*;
import static codeemoji.core.CEModifier.*;
import static com.intellij.psi.PsiModifier.*;

public record ShowingModifiersConfigurable(ShowingModifiersSettings settings) implements ImmediateConfigurable {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {

        var mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("Modifiers"));
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 5, 2, 5);

        //classes
        var classPanel = new JPanel();
        classPanel.setLayout(new GridLayout(10, 1));
        classPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder, "Classes"));

        var publicClass = new JCheckBox(PUBLIC_SYM.getEmoji() + PUBLIC, settings().isPublicClass());
        var defaultClass = new JCheckBox(DEFAULT_SYM.getEmoji() + DEFAULT, settings().isDefaultClass());
        var finalClass = new JCheckBox(FINAL_SYM.getEmoji() + FINAL, settings().isFinalClass());
        var abstractClass = new JCheckBox(ABSTRACT_SYM.getEmoji() + ABSTRACT, settings().isAbstractClass());

        addChangeListener(publicClass, PUBLIC_CLASS, changeListener);
        addChangeListener(abstractClass, ABSTRACT_CLASS, changeListener);
        addChangeListener(finalClass, FINAL_CLASS, changeListener);
        addChangeListener(defaultClass, DEFAULT_CLASS, changeListener);

        classPanel.add(publicClass, BorderLayout.CENTER);
        classPanel.add(defaultClass, BorderLayout.CENTER);
        classPanel.add(finalClass, BorderLayout.CENTER);
        classPanel.add(abstractClass, BorderLayout.CENTER);

        //fields
        var fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(10, 1));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder, "Fields"));

        var publicField = new JCheckBox(PUBLIC_SYM.getEmoji() + PUBLIC, settings().isPublicField());
        var defaultField = new JCheckBox(DEFAULT_SYM.getEmoji() + DEFAULT, settings().isDefaultField());
        var finalField = new JCheckBox(FINAL_SYM.getEmoji() + FINAL, settings().isFinalField());
        var protectedField = new JCheckBox(PROTECTED_SYM.getEmoji() + PROTECTED, settings().isProtectedField());
        var privateField = new JCheckBox(PRIVATE_SYM.getEmoji() + PRIVATE, settings().isPrivateField());
        var staticField = new JCheckBox(STATIC_SYM.getEmoji() + STATIC, settings().isStaticField());
        var transientField = new JCheckBox(TRANSIENT_SYM.getEmoji() + TRANSIENT, settings().isTransientField());
        var volatileField = new JCheckBox(VOLATILE_SYM.getEmoji() + VOLATILE, settings().isVolatileField());

        addChangeListener(publicField, PUBLIC_FIELD, changeListener);
        addChangeListener(protectedField, PROTECTED_FIELD, changeListener);
        addChangeListener(defaultField, DEFAULT_FIELD, changeListener);
        addChangeListener(privateField, PRIVATE_FIELD, changeListener);
        addChangeListener(finalField, FINAL_FIELD, changeListener);
        addChangeListener(staticField, STATIC_FIELD, changeListener);
        addChangeListener(transientField, TRANSIENT_FIELD, changeListener);
        addChangeListener(volatileField, VOLATILE_FIELD, changeListener);

        fieldsPanel.add(publicField, BorderLayout.CENTER);
        fieldsPanel.add(defaultField, BorderLayout.CENTER);
        fieldsPanel.add(finalField, BorderLayout.CENTER);
        fieldsPanel.add(protectedField, BorderLayout.CENTER);
        fieldsPanel.add(privateField, BorderLayout.CENTER);
        fieldsPanel.add(staticField, BorderLayout.CENTER);
        fieldsPanel.add(volatileField, BorderLayout.CENTER);
        fieldsPanel.add(transientField, BorderLayout.CENTER);

        //methods
        var methodsPanel = new JPanel();
        methodsPanel.setLayout(new GridLayout(10, 1));
        methodsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder, "Methods"));

        var publicMethod = new JCheckBox(PUBLIC_SYM.getEmoji() + PUBLIC, settings().isPublicMethod());
        var defaultMethod = new JCheckBox(DEFAULT_SYM.getEmoji() + DEFAULT, settings().isDefaultMethod());
        var finalMethod = new JCheckBox(FINAL_SYM.getEmoji() + FINAL, settings().isPrivateMethod());
        var abstractMethod = new JCheckBox(ABSTRACT_SYM.getEmoji() + ABSTRACT, settings().isAbstractMethod());
        var protectedMethod = new JCheckBox(PROTECTED_SYM.getEmoji() + PROTECTED, settings().isProtectedMethod());
        var privateMethod = new JCheckBox(PRIVATE_SYM.getEmoji() + PRIVATE, settings().isPrivateMethod());
        var staticMethod = new JCheckBox(STATIC_SYM.getEmoji() + STATIC, settings().isPrivateMethod());
        var synchronizedMethod = new JCheckBox(SYNCHRONIZED_SYM.getEmoji() + SYNCHRONIZED, settings().isSynchronizedMethod());
        var nativeMethod = new JCheckBox(NATIVE_SYM.getEmoji() + NATIVE, settings().isNativeMethod());
        var defaultInterfaceMethod = new JCheckBox(DEFAULT_INTERFACE_SYM.getEmoji() + DEFAULT + " (in interfaces)", settings().isDefaultInterfaceMethod());

        addChangeListener(publicMethod, PUBLIC_METHOD, changeListener);
        addChangeListener(protectedMethod, PROTECTED_METHOD, changeListener);
        addChangeListener(defaultMethod, DEFAULT_METHOD, changeListener);
        addChangeListener(privateMethod, PRIVATE_METHOD, changeListener);
        addChangeListener(staticMethod, STATIC_METHOD, changeListener);
        addChangeListener(finalMethod, FINAL_METHOD, changeListener);
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

    private void addChangeListener(@NotNull JCheckBox checkBox, @NotNull CEModifier ceModifier, @NotNull ChangeListener changeListener) {
        checkBox.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                var state = checkBox.isSelected();
                switch (ceModifier) {
                    //classes
                    case PUBLIC_CLASS -> {
                        settings().setPublicClass(state);
                    }
                    case ABSTRACT_CLASS -> {
                        settings().setAbstractClass(state);
                    }
                    case FINAL_CLASS -> {
                        settings().setFinalClass(state);
                    }
                    case DEFAULT_CLASS -> {
                        settings().setDefaultClass(state);
                    }
                    //fields
                    case PUBLIC_FIELD -> {
                        settings().setPublicField(state);
                    }
                    case PROTECTED_FIELD -> {
                        settings().setProtectedField(state);
                    }
                    case DEFAULT_FIELD -> {
                        settings().setDefaultField(state);
                    }
                    case PRIVATE_FIELD -> {
                        settings().setPrivateField(state);
                    }
                    case FINAL_FIELD -> {
                        settings().setFinalField(state);
                    }
                    case STATIC_FIELD -> {
                        settings().setStaticField(state);
                    }
                    case TRANSIENT_FIELD -> {
                        settings().setTransientField(state);
                    }
                    case VOLATILE_FIELD -> {
                        settings().setVolatileField(state);
                    }
                    //methods
                    case PUBLIC_METHOD -> {
                        settings().setPublicMethod(state);
                    }
                    case PROTECTED_METHOD -> {
                        settings().setProtectedMethod(state);
                    }
                    case DEFAULT_METHOD -> {
                        settings().setDefaultMethod(state);
                    }
                    case PRIVATE_METHOD -> {
                        settings().setPrivateMethod(state);
                    }
                    case STATIC_METHOD -> {
                        settings().setStaticMethod(state);
                    }
                    case FINAL_METHOD -> {
                        settings().setFinalMethod(state);
                    }
                    case ABSTRACT_METHOD -> {
                        settings().setAbstractMethod(state);
                    }
                    case SYNCHRONIZED_METHOD -> {
                        settings().setSynchronizedMethod(state);
                    }
                    case NATIVE_METHOD -> {
                        settings().setNativeMethod(state);
                    }
                    case DEFAULT_INTERFACE_METHOD -> {
                        settings().setDefaultInterfaceMethod(state);
                    }
                }
                changeListener.settingsChanged();
            }
        });
    }

}
