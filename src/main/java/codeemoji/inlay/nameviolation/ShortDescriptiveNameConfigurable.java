package codeemoji.inlay.nameviolation;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class ShortDescriptiveNameConfigurable extends CEConfigurableWindow<ShortDescriptiveNameSettings> {

    @Override
    public @NotNull JComponent createComponent(ShortDescriptiveNameSettings settings, Project project, Language language, ChangeListener changeListener) {
        var panel = super.createComponent(settings, project, language, changeListener);
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getNumberOfLetters());
        jSpinner.addChangeListener(event -> {
            settings.setNumberOfLetters((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        panel.add(FormBuilder.createFormBuilder()
                .addLabeledComponent("Number of letters", jSpinner)
                .getPanel());
        return panel;
    }

}
