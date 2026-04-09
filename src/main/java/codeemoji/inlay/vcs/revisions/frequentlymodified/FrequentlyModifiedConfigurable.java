package codeemoji.inlay.vcs.revisions.frequentlymodified;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FrequentlyModifiedConfigurable extends CEBaseConfigurableWindow<FrequentlyModifiedSettings> {

    @Override
    protected void buildForm(FormBuilder builder, FrequentlyModifiedSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);

        var daySelector = new JSpinner();
        daySelector.setValue(settings.getDaysTimeFrame());
        daySelector.addChangeListener(event -> {
            settings.setDaysTimeFrame((Integer) daySelector.getValue());
            changeListener.settingsChanged();
        });

        var modificationSelector = new JSpinner();
        modificationSelector.setValue(settings.getModifications());
        modificationSelector.addChangeListener(event -> {
            settings.setModifications((Integer) modificationSelector.getValue());
            changeListener.settingsChanged();
        });

        builder.addLabeledComponent(CEBundle.getString("inlay.frequentlymodified.settings.timeframe"), daySelector);
        builder.addLabeledComponent(CEBundle.getString("inlay.frequentlymodified.settings.modifications"), modificationSelector);
    }
}