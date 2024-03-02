package codeemoji.inlay.structuralanalysis.element.method;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public record StateChangingMethodConfigurable(StateChangingMethodSettings settings) implements ImmediateConfigurable {
    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings().isCheckMethodCallsForStateChangeApplied());
        checkBox.addChangeListener(event -> {
            settings().setCheckMethodCallsForStateChangeApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Follow method calls and recursively check state change", checkBox)
                .getPanel();
    }
}