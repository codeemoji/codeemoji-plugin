package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class LargeLineCountMethodConfigurable extends CEBaseConfigurableWindow<LargeLineCountMethodSettings> {

    @Override
    protected void buildForm(FormBuilder builder, LargeLineCountMethodSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isCommentExclusionApplied());
        checkBox.addChangeListener(event -> {
            settings.setCommentExclusionApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getLinesOfCode());
        jSpinner.addChangeListener(event -> {
            settings.setLinesOfCode((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });

        builder.addLabeledComponent(CEBundle.getString("inlay.largelinecountmethod.setting.comments"), checkBox);
        builder.addLabeledComponent(CEBundle.getString("inlay.largelinecountmethod.setting.lines"), jSpinner);
    }
}
