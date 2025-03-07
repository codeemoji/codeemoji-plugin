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

class LargeMethodCountClassConfigurable extends CEConfigurableWindow<LargeMethodCountClassSettings> {

    @Override
    public @NotNull JComponent createComponent(LargeMethodCountClassSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        var panel = super.createComponent(settings, preview, project, language, changeListener);
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getMethodCount());
        jSpinner.addChangeListener(event -> {
            settings.setMethodCount((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        panel.add(FormBuilder.createFormBuilder()
                .addLabeledComponent("Methods", jSpinner)
                .getPanel());
        return panel;
    }

}
