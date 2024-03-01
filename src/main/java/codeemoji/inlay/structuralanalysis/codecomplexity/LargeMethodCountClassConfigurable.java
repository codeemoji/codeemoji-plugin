package codeemoji.inlay.structuralanalysis.codecomplexity;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("UnstableApiUsage")
record LargeMethodCountClassConfigurable(LargeMethodCountClassSettings settings) implements ImmediateConfigurable {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings().getMethodCount());
        jSpinner.addChangeListener(event -> {
            settings().setMethodCount((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Methods", jSpinner)
                .getPanel();
    }

}
