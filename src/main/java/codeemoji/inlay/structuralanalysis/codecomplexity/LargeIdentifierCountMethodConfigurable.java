package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class LargeIdentifierCountMethodConfigurable extends CEConfigurableWindow<LargeIdentifierCountMethodSettings> {


    @Override
    public @NotNull JComponent createComponent(LargeIdentifierCountMethodSettings settings, Project project, Language language, ChangeListener changeListener) {
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getIdentifierCount());
        jSpinner.addChangeListener(event -> {
            settings.setIdentifierCount((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Identifiers", jSpinner)
                .getPanel();
    }
}