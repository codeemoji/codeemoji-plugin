package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class PureGetterMethodConfigurable extends CEBaseConfigurableWindow<PureGetterMethodSettings> {

    @Override
    protected void buildForm(FormBuilder builder, PureGetterMethodSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);
        var checkBox = new JCheckBox();
        checkBox.setSelected(settings.isJavaBeansNamingConventionApplied());
        checkBox.addChangeListener(event -> {
            settings.setJavaBeansNamingConventionApplied(checkBox.isSelected());
            changeListener.settingsChanged();
        });
        builder.addLabeledComponent(CEBundle.getString("inlay.puregettermethod.setting"), checkBox);
    }
}
