package codeemoji.core.config;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CEGlobalConfigurable implements Configurable {


    private JCheckBox cbMyExternalService;
    private JCheckBox cbSecondaryService;
    private JTextField tfOssApiToken;
    private JCheckBox cbFrontEmojiPlacement;

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return CEBundle.getString("codeemoji.configurable.name");
    }

    @Override
    public @Nullable JComponent createComponent() {
        var globalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var innerPanel = CEUtils.createBasicInnerPanel("codeemoji.configurable.title", 2, 1);
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        var analysersPanel = CEUtils.createBasicInnerPanel("codeemoji.configurable.external.analysers.title", 1, 1);
        analysersPanel.setLayout(new BoxLayout(analysersPanel, BoxLayout.Y_AXIS));
        cbMyExternalService = new JCheckBox(CEBundle.getString("codeemoji.configurable.external.analysers.scanner"));
        cbSecondaryService = new JCheckBox(CEBundle.getString("codeemoji.configurable.external.analysers.secondary"));
        analysersPanel.add(cbMyExternalService);
        analysersPanel.add(cbSecondaryService);
        innerPanel.add(analysersPanel);

        var apiTokenPanel = CEUtils.createBasicInnerPanel("codeemoji.configurable.oss.api.token", 1, 1);
        apiTokenPanel.setLayout(new BoxLayout(apiTokenPanel, BoxLayout.Y_AXIS));
        tfOssApiToken = new JTextField();
        tfOssApiToken.setText(CEGlobalSettings.getInstance().getOssApiToken());
        apiTokenPanel.add(tfOssApiToken);
        innerPanel.add(apiTokenPanel);

        cbFrontEmojiPlacement = new JCheckBox(CEBundle.getString("codeemoji.configurable.front_placement"));
        cbFrontEmojiPlacement.setSelected(CEGlobalSettings.getInstance().isFrontEmojiPlacement());
        innerPanel.add(cbFrontEmojiPlacement);

        globalPanel.add(innerPanel);
        return globalPanel;
    }

    @Override
    public boolean isModified() {
        Boolean myExternalServiceState = cbMyExternalService.isSelected();
        Boolean useSecondaryService = cbSecondaryService.isSelected();
        Boolean frontEmojiPlacement = cbFrontEmojiPlacement.isSelected();
        return (!myExternalServiceState.equals(CEGlobalSettings.getInstance().getMyExternalServiceState()) ||
                !useSecondaryService.equals(CEGlobalSettings.getInstance().getUseSecondaryVulnerabilityScanner()) ||
                !tfOssApiToken.getText().equals(CEGlobalSettings.getInstance().getOssApiToken()) ||
                !frontEmojiPlacement.equals(CEGlobalSettings.getInstance().isFrontEmojiPlacement()));
    }

    @Override
    public void apply() throws ConfigurationException {
        CEGlobalSettings.getInstance().setMyExternalServiceState(cbMyExternalService.isSelected());
        CEGlobalSettings.getInstance().setUseSecondaryVulnerabilityScanner(cbSecondaryService.isSelected());
        CEGlobalSettings.getInstance().setOssApiToken(tfOssApiToken.getText());
        CEGlobalSettings.getInstance().setFrontEmojiPlacement(cbFrontEmojiPlacement.isSelected());
        CEGlobalSettings.getInstance().fireSettingsChanged();
    }

    @Override
    public void reset() {
        cbMyExternalService.setSelected(CEGlobalSettings.getInstance().getMyExternalServiceState());
        cbSecondaryService.setSelected(CEGlobalSettings.getInstance().getUseSecondaryVulnerabilityScanner());
        tfOssApiToken.setText(CEGlobalSettings.getInstance().getOssApiToken());
        cbFrontEmojiPlacement.setSelected(CEGlobalSettings.getInstance().isFrontEmojiPlacement());
        CEGlobalSettings.getInstance().fireSettingsChanged();
    }

    @Override
    public void disposeUIResources() {
        cbMyExternalService = null;
        cbSecondaryService = null;
        tfOssApiToken = null;
        cbFrontEmojiPlacement = null;
    }
}
