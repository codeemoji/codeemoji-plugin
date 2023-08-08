package codeemoji.inlay.invisiblefeatures;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import java.awt.*;

import static com.intellij.psi.PsiModifier.*;

public record ShowingModifiersConfigurable(ShowingModifiersSettings settings) implements ImmediateConfigurable {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {

        var mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createTitledBorder("Modifiers"));
        Border emptyBorder = BorderFactory.createEmptyBorder(3, 6, 3, 6);

        //classes
        var classPanel = new JPanel();
        classPanel.setLayout(new GridLayout(9, 1));
        classPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder, "Classes"));
        var publicClass = new JCheckBox(PUBLIC, settings().isPublicClass());
        var abstractClass = new JCheckBox(ABSTRACT, settings().isAbstractClass());
        var finalClass = new JCheckBox(FINAL, settings().isFinalClass());
        var strictFPClass = new JCheckBox(STRICTFP, settings().isStrictFPClass());
        var defaultClass = new JCheckBox(DEFAULT, settings().isDefaultClass());
        addChangeListener(publicClass, "publicClass", changeListener);
        addChangeListener(abstractClass, "abstractClass", changeListener);
        addChangeListener(finalClass, "finalClass", changeListener);
        addChangeListener(strictFPClass, "strictFPClass", changeListener);
        addChangeListener(defaultClass, "defaultClass", changeListener);
        classPanel.add(publicClass, BorderLayout.CENTER);
        classPanel.add(abstractClass, BorderLayout.CENTER);
        classPanel.add(finalClass, BorderLayout.CENTER);
        classPanel.add(strictFPClass, BorderLayout.CENTER);
        classPanel.add(defaultClass, BorderLayout.CENTER);

        //fields
        var fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(9, 1));
        fieldsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder, "Fields"));
        var publicField = new JCheckBox(PUBLIC, settings().isPublicField());
        var protectedField = new JCheckBox(PROTECTED, settings().isProtectedField());
        var defaultField = new JCheckBox(DEFAULT, settings().isDefaultField());
        var privateField = new JCheckBox(PRIVATE, settings().isPrivateField());
        var finalField = new JCheckBox(FINAL, settings().isFinalField());
        var staticField = new JCheckBox(STATIC, settings().isStaticField());
        var transientField = new JCheckBox(TRANSIENT, settings().isTransientField());
        var volatileField = new JCheckBox(VOLATILE, settings().isVolatileField());
        var nativeField = new JCheckBox(NATIVE, settings().isNativeField());
        addChangeListener(publicField, "publicField", changeListener);
        addChangeListener(protectedField, "protectedField", changeListener);
        addChangeListener(defaultField, "defaultField", changeListener);
        addChangeListener(privateField, "privateField", changeListener);
        addChangeListener(finalField, "finalField", changeListener);
        addChangeListener(staticField, "staticField", changeListener);
        addChangeListener(transientField, "transientField", changeListener);
        addChangeListener(volatileField, "volatileField", changeListener);
        addChangeListener(nativeField, "nativeField", changeListener);
        fieldsPanel.add(publicField, BorderLayout.CENTER);
        fieldsPanel.add(protectedField, BorderLayout.CENTER);
        fieldsPanel.add(defaultField, BorderLayout.CENTER);
        fieldsPanel.add(privateField, BorderLayout.CENTER);
        fieldsPanel.add(finalField, BorderLayout.CENTER);
        fieldsPanel.add(staticField, BorderLayout.CENTER);
        fieldsPanel.add(transientField, BorderLayout.CENTER);
        fieldsPanel.add(volatileField, BorderLayout.CENTER);
        fieldsPanel.add(nativeField, BorderLayout.CENTER);

        //methods
        var methodsPanel = new JPanel();
        methodsPanel.setLayout(new GridLayout(9, 1));
        methodsPanel.setBorder(BorderFactory.createTitledBorder(emptyBorder, "Methods"));
        var publicMethod = new JCheckBox(PUBLIC, settings().isPublicMethod());
        var protectedMethod = new JCheckBox(PROTECTED, settings().isProtectedMethod());
        var defaultMethod = new JCheckBox(DEFAULT, settings().isDefaultMethod());
        var privateMethod = new JCheckBox(PRIVATE, settings().isPrivateMethod());
        var abstractMethod = new JCheckBox(ABSTRACT, settings().isAbstractMethod());
        var synchronizedMethod = new JCheckBox(SYNCHRONIZED, settings().isSynchronizedMethod());
        var nativeMethod = new JCheckBox(NATIVE, settings().isNativeMethod());
        var strictFPMethod = new JCheckBox(STRICTFP, settings().isStrictFPMethod());
        var defaultInInterfacesMethod = new JCheckBox(DEFAULT + " (in interfaces)", settings().isDefaultInInterfacesMethod());
        addChangeListener(publicMethod, "publicMethod", changeListener);
        addChangeListener(protectedMethod, "protectedMethod", changeListener);
        addChangeListener(defaultMethod, "defaultMethod", changeListener);
        addChangeListener(privateMethod, "privateMethod", changeListener);
        addChangeListener(abstractMethod, "abstractMethod", changeListener);
        addChangeListener(synchronizedMethod, "synchronizedMethod", changeListener);
        addChangeListener(nativeMethod, "nativeMethod", changeListener);
        addChangeListener(strictFPMethod, "strictFPMethod", changeListener);
        addChangeListener(defaultInInterfacesMethod, "defaultInInterfacesMethod", changeListener);
        methodsPanel.add(publicMethod, BorderLayout.CENTER);
        methodsPanel.add(protectedMethod, BorderLayout.CENTER);
        methodsPanel.add(defaultMethod, BorderLayout.CENTER);
        methodsPanel.add(privateMethod, BorderLayout.CENTER);
        methodsPanel.add(abstractMethod, BorderLayout.CENTER);
        methodsPanel.add(synchronizedMethod, BorderLayout.CENTER);
        methodsPanel.add(nativeMethod, BorderLayout.CENTER);
        methodsPanel.add(strictFPMethod, BorderLayout.CENTER);
        methodsPanel.add(defaultInInterfacesMethod, BorderLayout.CENTER);

        mainPanel.add(classPanel);
        mainPanel.add(fieldsPanel);
        mainPanel.add(methodsPanel);

        return FormBuilder.createFormBuilder()
                .addComponent(mainPanel)
                .getPanel();
    }

    private void addChangeListener(@NotNull JCheckBox checkBox, @NotNull String name, @NotNull ChangeListener changeListener) {
        checkBox.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                switch (name) {
                    //classes
                    case "publicClass" -> {
                        settings().setPublicClass(checkBox.isSelected());
                    }
                    case "abstractClass" -> {
                        settings().setAbstractClass(checkBox.isSelected());
                    }
                    case "finalClass" -> {
                        settings().setFinalClass(checkBox.isSelected());
                    }
                    case "strictFPClass" -> {
                        settings().setStrictFPClass(checkBox.isSelected());
                    }
                    case "defaultClass" -> {
                        settings().setDefaultClass(checkBox.isSelected());
                    }
                    //fields
                    case "publicField" -> {
                        settings().setPublicField(checkBox.isSelected());
                    }
                    case "protectedField" -> {
                        settings().setProtectedField(checkBox.isSelected());
                    }
                    case "defaultField" -> {
                        settings().setDefaultField(checkBox.isSelected());
                    }
                    case "privateField" -> {
                        settings().setPrivateField(checkBox.isSelected());
                    }
                    case "finalField" -> {
                        settings().setFinalField(checkBox.isSelected());
                    }
                    case "staticField" -> {
                        settings().setStaticField(checkBox.isSelected());
                    }
                    case "transientField" -> {
                        settings().setTransientField(checkBox.isSelected());
                    }
                    case "volatileField" -> {
                        settings().setVolatileField(checkBox.isSelected());
                    }
                    case "nativeField" -> {
                        settings().setNativeField(checkBox.isSelected());
                    }
                    //methods
                    case "publicMethod" -> {
                        settings().setPublicMethod(checkBox.isSelected());
                    }
                    case "protectedMethod" -> {
                        settings().setProtectedMethod(checkBox.isSelected());
                    }
                    case "defaultMethod" -> {
                        settings().setDefaultMethod(checkBox.isSelected());
                    }
                    case "privateMethod" -> {
                        settings().setPrivateMethod(checkBox.isSelected());
                    }
                    case "abstractMethod" -> {
                        settings().setAbstractMethod(checkBox.isSelected());
                    }
                    case "synchronizedMethod" -> {
                        settings().setSynchronizedMethod(checkBox.isSelected());
                    }
                    case "nativeMethod" -> {
                        settings().setNativeMethod(checkBox.isSelected());
                    }
                    case "strictFPMethod" -> {
                        settings().setStrictFPMethod(checkBox.isSelected());
                    }
                    case "defaultInInterfacesMethod" -> {
                        settings().setDefaultInInterfacesMethod(checkBox.isSelected());
                    }
                }
                changeListener.settingsChanged();
            }
        });
    }

}
