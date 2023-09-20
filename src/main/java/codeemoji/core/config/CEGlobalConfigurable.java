package codeemoji.core.config;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CEGlobalConfigurable implements Configurable {

    private JCheckBox cbMyExternalService;

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return CEBundle.getString("codeemoji.configurable.name");
    }

    @Override
    public @Nullable JComponent createComponent() {
        var globalPanel = new JPanel();
        var innerPanel = CEUtils.createBasicInnerPanel("codeemoji.configurable.title", 2, 1);
        var analysersPanel = CEUtils.createBasicInnerPanel("codeemoji.configurable.external.analysers.title", 1, 1);
        cbMyExternalService = new JCheckBox("MyExternalService");
        analysersPanel.add(cbMyExternalService);
        innerPanel.add(analysersPanel);
        globalPanel.add(innerPanel);
        return globalPanel;
    }

    @Override
    public boolean isModified() {
        Boolean myExternalServiceState = cbMyExternalService.isSelected();
        return !myExternalServiceState.equals(CEGlobalSettings.getInstance().getMyExternalServiceState());
    }

    @Override
    public void apply() throws ConfigurationException {
        Boolean myExternalServiceState = cbMyExternalService.isSelected();
        CEGlobalSettings.getInstance().setMyExternalServiceState(myExternalServiceState);
    }

    @Override
    public void reset() {
        cbMyExternalService.setSelected(CEGlobalSettings.getInstance().getMyExternalServiceState());
    }

    @Override
    public void disposeUIResources() {
        cbMyExternalService = null;
    }
}
