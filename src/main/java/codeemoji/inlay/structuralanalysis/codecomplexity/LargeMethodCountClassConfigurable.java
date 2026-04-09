package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEBundle;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class LargeMethodCountClassConfigurable extends CEBaseConfigurableWindow<LargeMethodCountClassSettings> {

    @Override
    protected void buildForm(FormBuilder builder, LargeMethodCountClassSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        super.buildForm(builder, settings, preview, project, language, changeListener);

        var jSpinner = new JSpinner();
        jSpinner.setValue(settings.getMethodCount());
        jSpinner.addChangeListener(event -> {
            settings.setMethodCount((Integer) jSpinner.getValue());
            changeListener.settingsChanged();
        });

        builder.addLabeledComponent(CEBundle.getString("inlay.largemethodcountclass.settings"), jSpinner);
    }
}
