package state;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

@RequiredArgsConstructor
public class BadVariableNameConfig implements ImmediateConfigurable {

    public static final String NAME = "Bad Variable Name";
    public static final String KEY = "badVariableNameHint";
    public static final String DESCRIPTION = "Show inlay hints for bad variable name";
    private final BadVariableNameState state;

    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        var jSpinner = new JSpinner();
        jSpinner.setValue(state.getNumberOfLetters());
        jSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                state.setNumberOfLetters((Integer) jSpinner.getValue());
                changeListener.settingsChanged();
            }
        });
        return FormBuilder.createFormBuilder()
                .addComponent(new JLabel(DESCRIPTION))
                .addLabeledComponent("Number of letters", jSpinner)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }
}
