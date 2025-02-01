package codeemoji.inlay.vulnerabilities;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class VulnerableDependencyConfigurable extends CEConfigurableWindow<VulnerableDependencySettings> {

    @Override
    public @NotNull JComponent createComponent(VulnerableDependencySettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        var panel = super.createComponent(settings, preview, project, language, changeListener);
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isCheckVulnerableDependencyApplied());
        checkBox.addChangeListener(event -> {
            settings.setCheckVulnerableDependencyApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        panel.add(FormBuilder.createFormBuilder()
                .addLabeledComponent("Follow method calls and recursively check vulnerability", checkBox)
                .getPanel());
        return panel;
    }
}


