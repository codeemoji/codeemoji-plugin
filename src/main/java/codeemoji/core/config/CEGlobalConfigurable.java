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
    private JCheckBox secondaryService;
    private JTextField tfOssApiToken;

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
        secondaryService = new JCheckBox("Use secondary Service for Vulnerable Methods scan");
        analysersPanel.add(cbMyExternalService);
        analysersPanel.add(secondaryService);
        innerPanel.add(analysersPanel);

        var apiTokenPanel = CEUtils.createBasicInnerPanel("codeemoji.configurable.oss.api.token", 1, 1);
        tfOssApiToken = new JTextField();
        apiTokenPanel.add(tfOssApiToken);
        innerPanel.add(apiTokenPanel);

        globalPanel.add(innerPanel);
        return globalPanel;
    }

    @Override
    public boolean isModified() {
        Boolean myExternalServiceState = cbMyExternalService.isSelected();
        Boolean useSecondaryService = secondaryService.isSelected();
        return (!myExternalServiceState.equals(CEGlobalSettings.getInstance().getMyExternalServiceState()) ||
                !useSecondaryService.equals(CEGlobalSettings.getInstance().getUseSecondaryVulnerabilityScanner()) ||
                !tfOssApiToken.getText().equals(CEGlobalSettings.getInstance().getOssApiToken()));
    }

    @Override
    public void apply() throws ConfigurationException {
        Boolean myExternalServiceState = cbMyExternalService.isSelected();
        Boolean useSecondaryService = secondaryService.isSelected();
        CEGlobalSettings.getInstance().setMyExternalServiceState(myExternalServiceState);
        CEGlobalSettings.getInstance().setUseSecondaryVulnerabilityScanner(useSecondaryService);
        CEGlobalSettings.getInstance().setOssApiToken(tfOssApiToken.getText());
        CEGlobalSettings.getInstance().fireSettingsChanged();
    }

    @Override
    public void reset() {
        cbMyExternalService.setSelected(CEGlobalSettings.getInstance().getMyExternalServiceState());
        secondaryService.setSelected(CEGlobalSettings.getInstance().getUseSecondaryVulnerabilityScanner());
        tfOssApiToken.setText(CEGlobalSettings.getInstance().getOssApiToken());
        CEGlobalSettings.getInstance().fireSettingsChanged();
    }

    @Override
    public void disposeUIResources() {
        cbMyExternalService = null;
        secondaryService = null;
        tfOssApiToken = null;
    }
}
