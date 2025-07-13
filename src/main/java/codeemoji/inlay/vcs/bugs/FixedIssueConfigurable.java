package codeemoji.inlay.vcs.bugs;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FixedIssueConfigurable extends CEBaseConfigurableWindow<FixedIssueSettings> {

    @Override
    public @NotNull JComponent createComponent(FixedIssueSettings settings, @Nullable String preview, Project project,
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