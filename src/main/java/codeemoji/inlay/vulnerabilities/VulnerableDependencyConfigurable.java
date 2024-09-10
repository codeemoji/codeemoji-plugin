package codeemoji.inlay.vulnerabilities;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

record VulnerableDependencyConfigurable(VulnerableDependencySettings settings) implements ImmediateConfigurable {
    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings().isCheckVulnerableDependecyApplied());
        checkBox.addChangeListener(event -> {
            settings().setCheckVulnerableDependecyApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Follow method calls and recursively check vulnerability", checkBox)
                .getPanel();
    }
}


