package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class StateChangingMethodConfigurable extends CEConfigurableWindow<StateChangingMethodSettings> {

    @Override
    public @NotNull JComponent createComponent(StateChangingMethodSettings settings, Project project, Language language, ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isCheckMethodCallsForStateChangeApplied());
        checkBox.addChangeListener(event -> {
            settings.setCheckMethodCallsForStateChangeApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Follow method calls and recursively check state change", checkBox)
                .getPanel();
    }
}