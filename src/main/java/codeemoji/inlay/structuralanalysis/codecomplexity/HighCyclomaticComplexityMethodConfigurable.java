package codeemoji.inlay.structuralanalysis.codecomplexity;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("UnstableApiUsage")
record HighCyclomaticComplexityMethodConfigurable(HighCyclomaticComplexityMethodSettings settings) implements ImmediateConfigurable {
    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
        var jSpinner = new JSpinner(new SpinnerNumberModel(0.00,0.00 ,10.00,0.01));
        var editor = new JSpinner.NumberEditor(jSpinner) ;
        jSpinner.setEditor(editor);
        jSpinner.setValue(settings().getCyclomaticComplexity());
        jSpinner.addChangeListener(event -> {
            settings().setCyclomaticComplexity((double) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Cyclomatic Complexity / Lines of Code", jSpinner)
                .getPanel();
    }
}
