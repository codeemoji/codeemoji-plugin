package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class LargeLineCountMethodConfigurable extends CEConfigurableWindow<LargeLineCountMethodSettings> {

    @Override
    public @NotNull JComponent createComponent(LargeLineCountMethodSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        var panel = super.createComponent(settings, preview, project, language, changeListener);
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
        panel.add(FormBuilder.createFormBuilder()
                .addLabeledComponent("Exclude comments from calculation", checkBox)
                .addLabeledComponent("Lines of Code", jSpinner)
                .getPanel());
        return panel;
    }

}
