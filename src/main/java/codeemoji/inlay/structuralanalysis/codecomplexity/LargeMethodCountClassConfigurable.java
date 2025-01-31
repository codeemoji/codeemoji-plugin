package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class LargeMethodCountClassConfigurable extends CEConfigurableWindow<LargeMethodCountClassSettings> {

    @Override
    public @NotNull JComponent createComponent(LargeMethodCountClassSettings settings, Project project, Language language, ChangeListener changeListener) {
        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getMethodCount());
        jSpinner.addChangeListener(event -> {
            settings.setMethodCount((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Methods", jSpinner)
                .getPanel();
    }

}
