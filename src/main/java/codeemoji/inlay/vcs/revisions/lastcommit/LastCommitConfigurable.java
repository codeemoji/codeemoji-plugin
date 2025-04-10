package codeemoji.inlay.vcs.revisions.lastcommit;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LastCommitConfigurable extends CEConfigurableWindow<LastCommitSettings> {

    @Override
    public @NotNull JComponent createComponent(LastCommitSettings settings, @Nullable String preview, Project project,
                                               Language language, ChangeListener changeListener) {
        var showDaysButton = new JCheckBox();
        showDaysButton.setSelected(settings.isShowDate());
        showDaysButton.addChangeListener(event -> {
            settings.setShowDate(showDaysButton.isSelected());
            changeListener.settingsChanged();
        });

        return FormBuilder.createFormBuilder()
                .addComponent(super.createComponent(settings, preview, project, language, changeListener))
                .addLabeledComponent(CEBundle.getString("inlay.lastcommit.settings.show_message"),
                        showDaysButton)
                .getPanel();
    }
}