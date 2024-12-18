package codeemoji.inlay.vcs.recentlymodified;

import codeemoji.core.base.CEBaseConfigurable;
import codeemoji.core.util.CEBundle;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class RecentlyModifiedConfigurable extends CEBaseConfigurable<RecentlyModifiedSettings> {
    public RecentlyModifiedConfigurable(RecentlyModifiedSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull JComponent createComponent(ChangeListener listener) {

        var daySelector = new JSpinner();
        daySelector.setValue(settings.getDays());
        daySelector.addChangeListener(event -> {
            settings.setDays((Integer) daySelector.getValue());
            listener.settingsChanged();
        });

        var showDaysButton = new JCheckBox();
        showDaysButton.setSelected(settings.isShowDate());
        showDaysButton.addChangeListener(event -> {
            settings.setShowDate(showDaysButton.isSelected());
            listener.settingsChanged();
        });

        return FormBuilder.createFormBuilder()
                .addComponent(super.createComponent(listener))
                .addLabeledComponent(CEBundle.getString("inlay.recentlymodified.settings.number_of_days"),
                        daySelector)
                .addLabeledComponent(CEBundle.getString("inlay.recentlymodified.settings.show_date"),
                        showDaysButton)
                .getPanel();
    }
}