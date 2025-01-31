package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

@SuppressWarnings("UnstableApiUsage")
class PureGetterMethodConfigurable extends CEConfigurableWindow<PureGetterMethodSettings>{

    @Override
    public @NotNull JComponent createComponent(PureGetterMethodSettings settings, Project project, Language language, ChangeListener changeListener) {
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isJavaBeansNamingConventionApplied());
        checkBox.addChangeListener(event -> {
            settings.setJavaBeansNamingConventionApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Apply JavaBeans naming convention", checkBox)
                .getPanel();
    }
}
