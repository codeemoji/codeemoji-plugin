package codeemoji.inlay.vcs.ownership.toomanyauthors;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TooManyAuthorsConfigurable extends CEBaseConfigurableWindow<TooManyAuthorsSettings> {

    @Override
    protected void buildForm(FormBuilder builder, TooManyAuthorsSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);

        var daySelector = new JSpinner();
        daySelector.setValue(settings.getMinimumAuthors());
        daySelector.addChangeListener(event -> {
            settings.setMinimumAuthors((Integer) daySelector.getValue());
            changeListener.settingsChanged();
        });

        builder.addLabeledComponent(CEBundle.getString("inlay.toomanyauthors.settings.minimum_authors"),
                daySelector);

    }
}