package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class LargeLineCountMethodConfigurable extends CEConfigurableWindow<LargeLineCountMethodSettings> {

    @Override
    public @NotNull JComponent createComponent(LargeLineCountMethodSettings settings, Project project, Language language, ChangeListener changeListener) {
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
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Exclude comments from calculation", checkBox)
                .addLabeledComponent("Lines of Code", jSpinner)
                .getPanel();
    }

}
