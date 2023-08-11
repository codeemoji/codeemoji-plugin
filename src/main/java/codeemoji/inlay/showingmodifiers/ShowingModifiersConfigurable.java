package codeemoji.inlay.showingmodifiers;

import codeemoji.core.CEBundle;
import codeemoji.core.CEProject;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Contract;
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
        var modifiersPanel = new JPanel();
        var projectsPanel = new JPanel();
        var classPanel = prepareClassPanel(changeListener);
        var fieldPanel = prepareFieldPanel(changeListener);
        var methodPanel = prepareMethodPanel(changeListener);
        var openProjectsPanel = prepareOpenProjectsPanel(changeListener);

        modifiersPanel.setBorder(BorderFactory.createTitledBorder(CEBundle.getString("inlay.showingmodifiers.options.title.modifiers")));
        projectsPanel.setBorder(BorderFactory.createTitledBorder(CEBundle.getString("inlay.showingmodifiers.options.title.openprojects")));
        projectsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        modifiersPanel.add(classPanel);
        modifiersPanel.add(fieldPanel);
        modifiersPanel.add(methodPanel);
        projectsPanel.add(openProjectsPanel);

        return FormBuilder.createFormBuilder()
                .addComponent(modifiersPanel)
                .addComponent(projectsPanel)
                .getPanel();
    }

    private @NotNull JPanel createBasicInnerPanel(@NotNull String typeTitle, int rows, int columns, boolean withBorder) {
        var result = new JPanel(new GridLayout(rows, columns));
        var title = CEBundle.getString("inlay.showingmodifiers.options.title." + typeTitle);
        if (withBorder) {
            result.setBorder(BorderFactory.createTitledBorder(title));
        } else {
            result.setBorder(BorderFactory.createTitledBorder(emptyBorder(), title));
        }
        return result;
    }

    private @NotNull JPanel createBasicInnerBagPanel(@NotNull String typeTitle, boolean withBorder) {
        var result = new JPanel(new GridBagLayout());
        var title = CEBundle.getString("inlay.showingmodifiers.options.title." + typeTitle);
        if (withBorder) {
            result.setBorder(BorderFactory.createTitledBorder(title));
        } else {
            result.setBorder(BorderFactory.createTitledBorder(emptyBorder(), title));
        }
        return result;
    }

    private @NotNull JPanel prepareClassPanel(@NotNull ChangeListener changeListener) {
        JPanel result = createBasicInnerPanel("classes", 10, 1, false);

        var publicClass = new JCheckBox(PUBLIC_SYMBOL_EMOJI + PUBLIC, settings().isPublicClass());
        var defaultClass = new JCheckBox(DEFAULT_SYMBOL_EMOJI + DEFAULT, settings().isDefaultClass());
        var finalClass = new JCheckBox(FINAL_SYMBOL_EMOJI + FINAL, settings().isFinalClass());
        var abstractClass = new JCheckBox(ABSTRACT_SYMBOL_EMOJI + ABSTRACT, settings().isAbstractClass());

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
        JPanel result = createBasicInnerPanel("fields", 10, 1, false);

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
        JPanel result = createBasicInnerPanel("methods", 10, 1, false);

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

    private @NotNull JPanel prepareOpenProjectsPanel(@NotNull ChangeListener changeListener) {
        var result = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        var selectProjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var classPanel = createBasicInnerBagPanel("classes", true);
        var fieldPanel = createBasicInnerBagPanel("fields", true);
        var methodPanel = createBasicInnerBagPanel("methods", true);
        var variablePanel = createBasicInnerBagPanel("variables", true);

        //selectProjectPanel
        var selectProjectLabel = new JLabel("Select: ");
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        ComboBox<CEProject> comboBox = new ComboBox<>();
        comboBox.addItem(new CEProject("", null));
        for (Project project : openProjects) {
            comboBox.addItem(new CEProject(project.getName(), project));
        }
        selectProjectPanel.add(selectProjectLabel, BorderLayout.CENTER);
        selectProjectPanel.add(comboBox, BorderLayout.CENTER);

        var classImplementsInterfaceLabel = new JLabel("Implements interface: ");
        var classExtendsSuperclassLabel = new JLabel("Extends superclass: ");
        var classPresenceOfAnnotationLabel = new JLabel("Presence of the annotation: ");
        var fieldPresenceOfAnnotationLabel = new JLabel("Presence of the annotation: ");
        var methodPresenceOfAnnotationLabel = new JLabel("Presence of the annotation: ");
        var fieldIsOfTypeLabel = new JLabel("Is of type: ");
        var variableIsOfTypeLabel = new JLabel("Is of type: ");
        var methodReturnsTypeLabel = new JLabel("Returns of the type: ");
        var textClassImplementsInterface = new JTextField(16);
        var textClassExtendsSuperclass = new JTextField(16);
        var textClassAnnotationPresence = new JTextField(16);
        var textFieldAnnotationPresence = new JTextField(16);
        var textFieldIsType = new JTextField(16);
        var textMethodAnnotationPresence = new JTextField(16);
        var textMethodReturnsType = new JTextField(16);
        var textVariableIsTypePanel = new JTextField(16);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        classPanel.add(classImplementsInterfaceLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        classPanel.add(textClassImplementsInterface, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        classPanel.add(classExtendsSuperclassLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        classPanel.add(textClassExtendsSuperclass, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        classPanel.add(classPresenceOfAnnotationLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        classPanel.add(textClassAnnotationPresence, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldPanel.add(fieldPresenceOfAnnotationLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        fieldPanel.add(textFieldAnnotationPresence, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldPanel.add(fieldIsOfTypeLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        fieldPanel.add(textFieldIsType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        methodPanel.add(methodPresenceOfAnnotationLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        methodPanel.add(textMethodAnnotationPresence, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        methodPanel.add(methodReturnsTypeLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        methodPanel.add(textMethodReturnsType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        variablePanel.add(variableIsOfTypeLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        variablePanel.add(textVariableIsTypePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        result.add(selectProjectPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        result.add(classPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        result.add(fieldPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        result.add(methodPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        result.add(variablePanel, gbc);

        return result;
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull Border emptyBorder() {
        return BorderFactory.createEmptyBorder(1, 3, 1, 3);
    }

    private void addChangeListener(@NotNull JCheckBox checkBox, @NotNull ShowingModifiers.Modifier modifier, @NotNull ChangeListener changeListener) {
        checkBox.addChangeListener(event -> {
            settings.change(modifier, checkBox.isSelected());
            changeListener.settingsChanged();
        });
    }

}
