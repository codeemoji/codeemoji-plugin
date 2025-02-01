package codeemoji.inlay.vcs.ownership;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TooManyAuthorsConfigurable extends CEConfigurableWindow<TooManyAuthorsSettings> {

    @Override
    public @NotNull JComponent createComponent(TooManyAuthorsSettings settings, @Nullable String preview, Project project,
                                               Language language, ChangeListener changeListener) {
        var daySelector = new JSpinner();
        daySelector.setValue(settings.getMinimumAuthors());
        daySelector.addChangeListener(event -> {
            settings.setMinimumAuthors((Integer) daySelector.getValue());
            changeListener.settingsChanged();
        });

        return FormBuilder.createFormBuilder()
                .addComponent(super.createComponent(settings, preview, project, language, changeListener))
                .addLabeledComponent(CEBundle.getString("inlay.toomanyauthors.settings.minimum_authors"),
                        daySelector)
                .getPanel();
    }
}