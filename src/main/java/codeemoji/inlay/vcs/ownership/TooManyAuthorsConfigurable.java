package codeemoji.inlay.vcs.ownership;

import codeemoji.core.config.CERuleFeature;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TooManyAuthorsConfigurable extends CEBaseConfigurableWindow<TooManyAuthorsSettings> {

    @Override
    public @NotNull JComponent createComponent(TooManyAuthorsSettings settings, @Nullable String preview, Project project,
                                               Language language, ChangeListener changeListener) {
        var daySelector = new JSpinner();
        daySelector.setValue(settings.getMinimumAuthors());
        daySelector.addChangeListener(event -> {
            settings.setMinimumAuthors((Integer) daySelector.getValue());
            changeListener.settingsChanged();
        });

        var enumSelector = new ComboBox<>(CERuleFeature.values());

        return FormBuilder.createFormBuilder()
                .addComponent(super.createComponent(settings, preview, project, language, changeListener))
                .addLabeledComponent(CEBundle.getString("inlay.toomanyauthors.settings.minimum_authors"),
                        daySelector)
                .addLabeledComponent("Display Mode", enumSelector) // Optional: use a localized string
                .getPanel();
    }
}