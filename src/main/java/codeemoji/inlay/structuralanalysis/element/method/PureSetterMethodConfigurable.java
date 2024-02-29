package codeemoji.inlay.structuralanalysis.element.method;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("UnstableApiUsage")
record PureSetterMethodConfigurable(PureSetterMethodSettings settings) implements ImmediateConfigurable {
    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings().isJavaBeansNamingConventionApplied());
        checkBox.addChangeListener(event -> {
            settings().setJavaBeansNamingConventionApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Apply JavaBeans naming convention", checkBox)
                .getPanel();
    }
}
