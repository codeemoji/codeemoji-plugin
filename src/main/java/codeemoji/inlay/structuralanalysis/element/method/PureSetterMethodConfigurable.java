package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class PureSetterMethodConfigurable extends CEBaseConfigurableWindow<PureSetterMethodSettings> {

    @Override
    public @NotNull JComponent createComponent(PureSetterMethodSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        var panel = super.createComponent(settings, preview, project, language, changeListener);
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isJavaBeansNamingConventionApplied());
        checkBox.addChangeListener(event -> {
            settings.setJavaBeansNamingConventionApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        panel.add(FormBuilder.createFormBuilder()
                .addLabeledComponent("Apply JavaBeans naming convention", checkBox)
                .getPanel());
        return panel;
    }
}
