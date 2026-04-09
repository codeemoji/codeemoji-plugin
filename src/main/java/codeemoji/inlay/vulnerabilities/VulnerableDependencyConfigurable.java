package codeemoji.inlay.vulnerabilities;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class VulnerableDependencyConfigurable extends CEBaseConfigurableWindow<VulnerableDependencySettings> {

    @Override
    protected void buildForm(FormBuilder builder, VulnerableDependencySettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isCheckVulnerableDependencyApplied());
        checkBox.addChangeListener(event -> {
            settings.setCheckVulnerableDependencyApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        builder.addLabeledComponent(CEBundle.getString("inlay.vulnerabledependency.settings"), checkBox);
    }
}


