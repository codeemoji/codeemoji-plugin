package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class ExternalFunctionalityInvokingMethodConfigurable extends CEConfigurableWindow<ExternalFunctionalityInvokingMethodSettings> {

    @Override
    public @NotNull JComponent createComponent(ExternalFunctionalityInvokingMethodSettings settings, Project project, Language language, ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isCheckMethodCallsForExternalityApplied());
        checkBox.addChangeListener(event -> {
            settings.setCheckMethodCallsForExternalityApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Follow method calls and recursively check externality", checkBox)
                .getPanel();
    }
}