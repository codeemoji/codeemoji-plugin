package codeemoji.inlay.vcs.revisions.unfrequentlymodified;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class UnFrequentlyModifiedConfigurable extends CEBaseConfigurableWindow<UnFrequentlyModifiedSettings> {

    @Override
    protected void buildForm(FormBuilder builder, UnFrequentlyModifiedSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);
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
        builder.addLabeledComponent(CEBundle.getString("inlay.recentlymodified.settings.number_of_days"), daySelector);
        builder.addLabeledComponent(CEBundle.getString("inlay.recentlymodified.settings.show_date"), showDaysButton);
    }
}