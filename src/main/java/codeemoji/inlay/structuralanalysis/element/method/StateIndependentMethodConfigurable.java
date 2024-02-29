package codeemoji.inlay.structuralanalysis.element.method;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public record StateIndependentMethodConfigurable(StateIndependentMethodSettings settings) implements ImmediateConfigurable {
    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings().isCheckMethodCallsForStateIndependenceApplied());
        checkBox.addChangeListener(event -> {
            settings().setCheckMethodCallsForStateIndependenceApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Follow method calls and recursively check state change", checkBox)
                .getPanel();
    }
}