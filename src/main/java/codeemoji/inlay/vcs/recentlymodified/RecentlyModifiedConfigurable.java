package codeemoji.inlay.vcs.recentlymodified;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class RecentlyModifiedConfigurable extends CEConfigurableWindow<RecentlyModifiedSettings> {

    @Override
    public @NotNull JComponent createComponent(RecentlyModifiedSettings settings, @Nullable String preview, Project project,
                                               Language language, ChangeListener changeListener) {
        var daySelector = new JSpinner();
        daySelector.setValue(settings.getDays());
        daySelector.addChangeListener(event -> {
            settings.setDays((Integer) daySelector.getValue());
            changeListener.settingsChanged();
        });

        var showDaysButton = new JCheckBox();
        showDaysButton.setSelected(settings.isShowDate());
        showDaysButton.addChangeListener(event -> {
            settings.setShowDate(showDaysButton.isSelected());
            changeListener.settingsChanged();
        });

        return FormBuilder.createFormBuilder()
                .addComponent(super.createComponent(settings, preview, project, language, changeListener))
                .addLabeledComponent(CEBundle.getString("inlay.recentlymodified.settings.number_of_days"),
                        daySelector)
                .addLabeledComponent(CEBundle.getString("inlay.recentlymodified.settings.show_date"),
                        showDaysButton)
                .getPanel();
    }
}