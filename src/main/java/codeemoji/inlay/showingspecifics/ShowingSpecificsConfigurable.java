package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEIProjectConfig;
import codeemoji.core.collector.project.config.CEConfigFile;
import codeemoji.core.collector.project.config.CERuleFeature;
import codeemoji.core.util.CEBundle;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CERuleElement.*;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

public record ShowingSpecificsConfigurable(ShowingSpecificsSettings settings) implements ImmediateConfigurable, CEIProjectConfig {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
        var specificsPanel = new JPanel();
        var detailProject = prepareOpenProjectsPanel();
        specificsPanel.add(detailProject);
        return FormBuilder.createFormBuilder()
                .addComponent(specificsPanel)
                .getPanel();
    }

    private @NotNull JPanel prepareOpenProjectsPanel() {
        Project project = getOpenProject();
        if (!project.isDisposed()) {
            return readConfigsForOpenProject();
        }
        return new JPanel();
    }

    private @NotNull JPanel readConfigsForOpenProject() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = HORIZONTAL;

        String projectStr = CEBundle.getString("inlay.showingspecifics.options.title.project");
        String loadedRulesStr = CEBundle.getString("inlay.showingspecifics.options.title.loaded_rules");

        JPanel result = createBasicInnerBagPanel(loadedRulesStr + " - " + projectStr + ": " + getOpenProject().getName(), false);

        JPanel classPanel = createBasicInnerBagPanel("Classes", true);
        var features = readRuleFeatures(CLASS);
        buildInnerFeaturePanel(features, classPanel);

        JPanel fieldsPanel = createBasicInnerBagPanel("Fields", true);
        features = readRuleFeatures(FIELD);
        buildInnerFeaturePanel(features, fieldsPanel);

        JPanel methodsPanel = createBasicInnerBagPanel("Methods", true);
        features = readRuleFeatures(METHOD);
        buildInnerFeaturePanel(features, methodsPanel);

        JPanel parametersPanel = createBasicInnerBagPanel("Parameters", true);
        features = readRuleFeatures(PARAMETER);
        buildInnerFeaturePanel(features, parametersPanel);

        JPanel localVariablesPanel = createBasicInnerBagPanel("Local Variables", true);
        features = readRuleFeatures(LOCALVARIABLE);
        buildInnerFeaturePanel(features, localVariablesPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        result.add(classPanel, gbc);
        gbc.gridy = 1;
        result.add(fieldsPanel, gbc);
        gbc.gridy = 2;
        result.add(methodsPanel, gbc);
        gbc.gridy = 3;
        result.add(parametersPanel, gbc);
        gbc.gridy = 4;
        result.add(localVariablesPanel, gbc);

        return result;
    }

    private void buildInnerFeaturePanel(@NotNull Map<CERuleFeature, List<String>> features, @NotNull JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = WEST;
        int gridX = 0;
        int gridY = 0;
        for (var entry : features.entrySet()) {
            var key = new JLabel(entry.getKey().getValue() + ": ");
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

    private Project getOpenProject() {
        return ProjectManager.getInstance().getOpenProjects()[0];
    }

    private @NotNull JPanel createBasicInnerBagPanel(@NotNull String title, boolean withBorder) {
        var result = new JPanel(new GridBagLayout());
        if (withBorder) {
            result.setBorder(BorderFactory.createTitledBorder(title));
        } else {
            result.setBorder(BorderFactory.createTitledBorder(emptyBorder(), title));
        }
        return result;
    }

    @Contract(value = " -> new", pure = true)
    private @NotNull Border emptyBorder() {
        return BorderFactory.createEmptyBorder(1, 3, 1, 3);
    }

    @Contract(" -> new")
    @Override
    public @NotNull CEConfigFile getConfigFile() {
        return new CEConfigFile(getOpenProject());
    }

    @Override
    public Object getConfig(String key) {
        return getConfigFile().getConfigs().get(key);
    }
}
