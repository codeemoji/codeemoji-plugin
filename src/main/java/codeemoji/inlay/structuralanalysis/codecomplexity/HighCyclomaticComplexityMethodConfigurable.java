package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class HighCyclomaticComplexityMethodConfigurable extends CEConfigurableWindow<HighCyclomaticComplexityMethodSettings> {

    @Override
    public @NotNull JComponent createComponent(HighCyclomaticComplexityMethodSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        var panel = super.createComponent(settings, preview, project, language, changeListener);
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

        //TODO: translation here
        panel.add(FormBuilder.createFormBuilder()
                .addLabeledComponent(CEBundle.getString("inlay.highcyclomaticcomplexitymethod.settings.threshold"), jSpinnerCyclomaticComplexityThreshold)
                .addLabeledComponent(CEBundle.getString("inlay.highcyclomaticcomplexitymethod.settings.linethreshold"), jSpinnerLineCountStartThreshold)
                .addLabeledComponent(CEBundle.getString("inlay.highcyclomaticcomplexitymethod.settings.ratio"), jSpinnerCyclomaticComplexityPerLine)
                .getPanel());

        return panel;
    }
}
