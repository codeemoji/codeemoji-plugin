package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class HighCyclomaticComplexityMethodConfigurable extends CEConfigurableWindow<HighCyclomaticComplexityMethodSettings> {

    @Override
    public @NotNull JComponent createComponent(HighCyclomaticComplexityMethodSettings settings, Project project, Language language, ChangeListener changeListener) {

        var jSpinnerCyclomaticComplexityThreshold = new JSpinner();
        jSpinnerCyclomaticComplexityThreshold.setValue(settings.getCyclomaticComplexityThreshold());
        jSpinnerCyclomaticComplexityThreshold.addChangeListener(event -> {
            settings.setCyclomaticComplexityThreshold((Integer) jSpinnerCyclomaticComplexityThreshold.getValue());
            changeListener.settingsChanged();
        });

        var jSpinnerLineCountStartThreshold = new JSpinner();
        jSpinnerLineCountStartThreshold.setValue(settings.getLineCountStartThreshold());
        jSpinnerLineCountStartThreshold.addChangeListener(event -> {
            settings.setLineCountStartThreshold((Integer) jSpinnerLineCountStartThreshold.getValue());
            changeListener.settingsChanged();
        });

        var jSpinnerCyclomaticComplexityPerLine = new JSpinner(new SpinnerNumberModel(0.00,0.00 ,10.00,0.01));
        var editor = new JSpinner.NumberEditor(jSpinnerCyclomaticComplexityPerLine) ;
        jSpinnerCyclomaticComplexityPerLine.setEditor(editor);
        jSpinnerCyclomaticComplexityPerLine.setValue(settings.getCyclomaticComplexityPerLine());
        jSpinnerCyclomaticComplexityPerLine.addChangeListener(event -> {
            settings.setCyclomaticComplexityPerLine((double) jSpinnerCyclomaticComplexityPerLine.getValue());
            changeListener.settingsChanged();
        });

        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Cyclomatic Complexity Threshold", jSpinnerCyclomaticComplexityThreshold)
                .addLabeledComponent("Line Count Threshold", jSpinnerLineCountStartThreshold)
                .addLabeledComponent("Cyclomatic Complexity / Lines of Code", jSpinnerCyclomaticComplexityPerLine)
                .getPanel();
    }
}
