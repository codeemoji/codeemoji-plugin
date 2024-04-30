package codeemoji.inlay.structuralanalysis.codecomplexity;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("UnstableApiUsage")
record LargeLineCountMethodConfigurable(LargeLineCountMethodSettings settings) implements ImmediateConfigurable {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings().isCommentExclusionApplied());
        checkBox.addChangeListener(event -> {
            settings().setCommentExclusionApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings().getLinesOfCode());
        jSpinner.addChangeListener(event -> {
            settings().setLinesOfCode((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Exclude comments from calculation", checkBox)
                .addLabeledComponent("Lines of Code", jSpinner)
                .getPanel();
    }

}
