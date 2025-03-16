package codeemoji.inlay.vcs.revisions.frequentlymodified;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FrequentlyModifiedConfigurable extends CEConfigurableWindow<FrequentlyModifiedSettings> {

    @Override
    public @NotNull JComponent createComponent(FrequentlyModifiedSettings settings, @Nullable String preview, Project project,
                                               Language language, ChangeListener changeListener) {
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


        return FormBuilder.createFormBuilder()
                .addComponent(super.createComponent(settings, preview, project, language, changeListener))
                .addLabeledComponent(CEBundle.getString("inlay.frequentlymodified.settings.timeframe"),
                        daySelector)
                .addLabeledComponent(CEBundle.getString("inlay.frequentlymodified.settings.modifications"),
                        modificationSelector)
                .getPanel();
    }
}