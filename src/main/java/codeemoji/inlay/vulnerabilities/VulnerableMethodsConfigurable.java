package codeemoji.inlay.vulnerabilities;

import codeemoji.inlay.structuralanalysis.element.method.ExternalFunctionalityInvokingMethodSettings;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

record VulnerableMethodsConfigurable(VulnerableMethodsSettings settings) implements ImmediateConfigurable {
    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings().isCheckMethodCallsForExternalityApplied());
        checkBox.addChangeListener(event -> {
            settings().setCheckMethodCallsForExternalityApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Follow method calls and recursively check vulnerability", checkBox)
                .getPanel();
    }
}


