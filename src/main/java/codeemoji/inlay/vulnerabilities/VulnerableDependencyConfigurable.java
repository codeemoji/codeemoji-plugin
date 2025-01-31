package codeemoji.inlay.vulnerabilities;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

class VulnerableDependencyConfigurable extends CEConfigurableWindow<VulnerableDependencySettings> {

    @Override
    public @NotNull JComponent createComponent(VulnerableDependencySettings settings, Project project, Language language, ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isCheckVulnerableDependecyApplied());
        checkBox.addChangeListener(event -> {
            settings.setCheckVulnerableDependecyApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Follow method calls and recursively check vulnerability", checkBox)
                .getPanel();
    }
}


