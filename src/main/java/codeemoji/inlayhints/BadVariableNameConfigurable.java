package codeemoji.inlayhints;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public record BadVariableNameConfigurable(String header,
                                          BadVariableNameSettings settings) implements ImmediateConfigurable {

    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings().getNumberOfLetters());
        jSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                settings().setNumberOfLetters((Integer) jSpinner.getValue());
                changeListener.settingsChanged();
            }
        });
        return FormBuilder.createFormBuilder()
                .addComponent(new JLabel(header()))
                .addLabeledComponent("Number of letters", jSpinner)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }
}
