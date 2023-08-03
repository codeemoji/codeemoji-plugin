package codeemoji.inlay;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

public record ShortDescriptiveNameConfigurable(ShortDescriptiveNameSettings settings) implements ImmediateConfigurable {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings().getNumberOfLetters());
        jSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent event) {
                settings().setNumberOfLetters((Integer) jSpinner.getValue());
                changeListener.settingsChanged();
            }
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Number of letters", jSpinner)
                .getPanel();
    }

}
