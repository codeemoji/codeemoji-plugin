package codeemoji.inlay.nameviolation;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class ShortDescriptiveNameConfigurable extends CEBaseConfigurableWindow<ShortDescriptiveNameSettings> {

    @Override
    protected void buildForm(FormBuilder builder, ShortDescriptiveNameSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getNumberOfLetters());
        jSpinner.addChangeListener(event -> {
            settings.setNumberOfLetters((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        builder.addLabeledComponent(CEBundle.getString("inlay.shortdescriptivename.settings"), jSpinner);
    }
}
