package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEProjectConfig;
import codeemoji.core.collector.project.ProjectRuleSymbol;
import codeemoji.core.config.CEConfigFile;
import codeemoji.core.config.CERuleElement;
import codeemoji.core.config.CERuleFeature;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static codeemoji.core.config.CERuleElement.CLASS;
import static codeemoji.core.config.CERuleElement.FIELD;
import static codeemoji.core.config.CERuleElement.LOCALVARIABLE;
import static codeemoji.core.config.CERuleElement.METHOD;
import static codeemoji.core.config.CERuleElement.PARAMETER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

@SuppressWarnings("UnstableApiUsage")
public record ShowingSpecificsConfigurable(
        ShowingSpecificsSettings settings) implements ImmediateConfigurable, CEProjectConfig {

    private static @NotNull JPanel createBasicInnerBagPanel(@NotNull String title) {
        var result = new JPanel(new GridBagLayout());
        result.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0), title));
        return result;
    }

    private static @Nullable Project getOpenProject() {
        @NotNull Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (0 < openProjects.length) {
            return openProjects[0];
        }
        return null;
    }

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
        var specificsPanel = new JPanel();
        var detailProject = initOpenProjectsPanel();
        specificsPanel.add(detailProject);
        return FormBuilder.createFormBuilder()
                .addComponent(specificsPanel)
                .getPanel();
    }

    private @NotNull JComponent initOpenProjectsPanel() {
        var project = getOpenProject();
        var file = new CEConfigFile(project);
        if (file.getRules().isEmpty()) {
            return howToConfigurePanel();
        }
        if (null != project && !project.isDisposed()) {
            return buildPanelsForOpenProject(project);
        } else {
            return new JPanel();
        }
    }

    private @NotNull JComponent howToConfigurePanel() {
        var panel = new JPanel();
        var noRuleLoaded = CEBundle.getString("inlay.showingspecifics.options.title.noruleloaded");
        var howToConfigure = CEBundle.getString("inlay.showingspecifics.options.title.noruleloaded.howtoconfigure");
        panel.add(new JLabel(noRuleLoaded + ":"));
        var button = new JButton();
        button.setText(new CESymbol(0x1F575).getEmoji() + " " + howToConfigure);
        button.addActionListener(event -> {
            try {
                BrowserUtil.browse(new URI(settings().getHowToConfigureURL()));
            } catch (URISyntaxException ex) {
                Logger LOG = Logger.getInstance(ShowingSpecificsConfigurable.class);
                LOG.info(ex);
            }
        });
        panel.add(button);
        return panel;
    }

    private @NotNull JPanel buildPanelsForOpenProject(@NotNull Project project) {
        var gbc = new GridBagConstraints();
        gbc.fill = HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        var projectStr = CEBundle.getString("inlay.showingspecifics.options.title.project");
        var loadedRulesStr = CEBundle.getString("inlay.showingspecifics.options.title.loaded_rules");
        var classTitle = CEBundle.getString("inlay.showingspecifics.options.title.classes");
        var fieldTitle = CEBundle.getString("inlay.showingspecifics.options.title.fields");
        var methodTitle = CEBundle.getString("inlay.showingspecifics.options.title.methods");
        var parameterTitle = CEBundle.getString("inlay.showingspecifics.options.title.parameters");
        var localVariableTitle = CEBundle.getString("inlay.showingspecifics.options.title.localvariables");

        var result = createBasicInnerBagPanel(loadedRulesStr + " (" + projectStr + ": " + project.getName() + ")");

        buildInnerElementPanel(result, gbc, CLASS, classTitle);
        buildInnerElementPanel(result, gbc, FIELD, fieldTitle);
        buildInnerElementPanel(result, gbc, METHOD, methodTitle);
        buildInnerElementPanel(result, gbc, PARAMETER, parameterTitle);
        buildInnerElementPanel(result, gbc, LOCALVARIABLE, localVariableTitle);

        return result;
    }

    private void buildInnerElementPanel(@NotNull JPanel result, @NotNull GridBagConstraints gbc,
                                        @NotNull CERuleElement elementRule, @NotNull String panelTitle) {
        var panel = createBasicInnerBagPanel(panelTitle);
        var features = readRuleFeatures(elementRule);
        if (!features.isEmpty()) {
            buildInnerFeaturePanel(elementRule, features, panel);
            result.add(panel, gbc);
            gbc.gridy++;
        }
    }

    private void buildInnerFeaturePanel(@NotNull CERuleElement elementRule,
                                        @NotNull Map<CERuleFeature, List<String>> features,
                                        @NotNull JPanel panel) {
        var gbc = new GridBagConstraints();
        gbc.anchor = WEST;
        var gridX = 0;
        var gridY = 0;
        for (var entry : features.entrySet()) {
            var feature = entry.getKey();
            var defaultSymbol = ProjectRuleSymbol.detectDefaultSymbol(feature);
            var symbol = readRuleEmoji(elementRule, feature, defaultSymbol);
            var key = new JLabel(symbol.getEmoji() + " " + feature.getValue() + ": ");
            var valuesStr = entry.getValue().toString();
            valuesStr = valuesStr.replace("[", "").replace("]", "");
            var value = new JTextField(valuesStr);
            value.setColumns(20);
            value.setEditable(false);
            gbc.gridx = gridX;
            gbc.gridy = gridY;
            panel.add(key, gbc);
            gridX++;
            gbc.gridx = gridX;
            panel.add(value, gbc);
            gridX--;
            gridY++;
        }
    }

    @Contract(" -> new")
    @Override
    public @NotNull CEConfigFile getConfigFile() {
        return new CEConfigFile(getOpenProject());
    }

    @SuppressWarnings("unused")
    @Override
    public Object readConfig(String key) {
        return getConfigFile().getConfigs().get(key);
    }
}
