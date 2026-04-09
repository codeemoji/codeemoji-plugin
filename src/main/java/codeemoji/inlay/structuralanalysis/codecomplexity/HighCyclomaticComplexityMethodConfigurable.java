package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class HighCyclomaticComplexityMethodConfigurable extends CEBaseConfigurableWindow<HighCyclomaticComplexityMethodSettings> {

    @Override
    protected void buildForm(FormBuilder builder, HighCyclomaticComplexityMethodSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);
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

        builder.addLabeledComponent(CEBundle.getString("inlay.highcyclomaticcomplexitymethod.settings.threshold"), jSpinnerCyclomaticComplexityThreshold);
        builder.addLabeledComponent(CEBundle.getString("inlay.highcyclomaticcomplexitymethod.settings.linethreshold"), jSpinnerLineCountStartThreshold);
        builder.addLabeledComponent(CEBundle.getString("inlay.highcyclomaticcomplexitymethod.settings.ratio"), jSpinnerCyclomaticComplexityPerLine);
    }
}
