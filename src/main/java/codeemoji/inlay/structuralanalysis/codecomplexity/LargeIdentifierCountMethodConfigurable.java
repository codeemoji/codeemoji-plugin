package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class LargeIdentifierCountMethodConfigurable extends CEConfigurableWindow<LargeIdentifierCountMethodSettings> {


    @Override
    public @NotNull JComponent createComponent(LargeIdentifierCountMethodSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        var panel = super.createComponent(settings, preview, project, language, changeListener);
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getIdentifierCount());
        jSpinner.addChangeListener(event -> {
            settings.setIdentifierCount((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        panel.add(FormBuilder.createFormBuilder()
                .addLabeledComponent("Identifiers", jSpinner)
                .getPanel());

        return panel;
    }
}