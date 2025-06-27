package codeemoji.inlay.vcs.refactors;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import codeemoji.inlay.vcs.bugs.FixedIssueSettings;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NameChangedConfigurable extends CEConfigurableWindow<NameChangedSettings> {

    @Override
    public @NotNull JComponent createComponent(NameChangedSettings settings, @Nullable String preview, Project project,
                                               Language language, ChangeListener changeListener) {

        var revisionsSelector = new JSpinner();
        revisionsSelector.setValue(settings.getMaxRevisions());
        revisionsSelector.addChangeListener(event -> {
            settings.setMaxRevisions((Integer) revisionsSelector.getValue());
            changeListener.settingsChanged();
        });

        // Create the form builder and add components
        return FormBuilder.createFormBuilder()
                .addComponent(super.createComponent(settings, preview, project, language, changeListener))
                .addLabeledComponent(CEBundle.getString("inlay.fixedissue.settings.max_revisions"),
                        revisionsSelector)
                .getPanel();
    }
}