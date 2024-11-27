package codeemoji.inlay.vcs.recentlymodified;

import codeemoji.core.base.CEBaseConfigurable;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RecentlyModifiedConfigurable extends CEBaseConfigurable<RecentlyModifiedSettings> {
    public RecentlyModifiedConfigurable(RecentlyModifiedSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull JComponent createComponent(ChangeListener listener) {
        var panel = super.createComponent(listener);

        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getDays());
        jSpinner.addChangeListener(event -> {
            settings.setDays((Integer) jSpinner.getValue());
            listener.settingsChanged();
        });
        var form = FormBuilder.createFormBuilder()
                .addLabeledComponent("Number of letters", jSpinner)
                .getPanel();

        panel.add(form);

        return panel;
    }
}