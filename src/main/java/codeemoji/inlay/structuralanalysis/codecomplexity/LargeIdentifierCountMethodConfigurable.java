package codeemoji.inlay.structuralanalysis.codecomplexity;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("UnstableApiUsage")
record LargeIdentifierCountMethodConfigurable(LargeIdentifierCountMethodSettings settings) implements ImmediateConfigurable {


    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings().getIdentifierCount());
        jSpinner.addChangeListener(event -> {
            settings().setIdentifierCount((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Identifiers", jSpinner)
                .getPanel();
    }
}