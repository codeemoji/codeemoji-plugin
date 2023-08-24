package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEIProjectConfig;
import codeemoji.core.collector.project.ProjectRuleSymbol;
import codeemoji.core.collector.project.config.CEConfigFile;
import codeemoji.core.collector.project.config.CERuleElement;
import codeemoji.core.collector.project.config.CERuleFeature;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        var detailProject = initOpenProjectsPanel();
        specificsPanel.add(detailProject);
        return FormBuilder.createFormBuilder()
                .addComponent(specificsPanel)
                .getPanel();
    }

    private @NotNull JPanel initOpenProjectsPanel() {
        Project project = getOpenProject();
        if (project != null && !project.isDisposed()) {
            return buildPanelsForOpenProject(project);
        }
        return new JPanel();
    }

    private @NotNull JPanel buildPanelsForOpenProject(@NotNull Project project) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        String projectStr = CEBundle.getString("inlay.showingspecifics.options.title.project");
        String loadedRulesStr = CEBundle.getString("inlay.showingspecifics.options.title.loaded_rules");
        String classTitle = CEBundle.getString("inlay.showingspecifics.options.title.classes");
        String fieldTitle = CEBundle.getString("inlay.showingspecifics.options.title.fields");
        String methodTitle = CEBundle.getString("inlay.showingspecifics.options.title.methods");
        String parameterTitle = CEBundle.getString("inlay.showingspecifics.options.title.parameters");
        String localVariableTitle = CEBundle.getString("inlay.showingspecifics.options.title.localvariables");

        JPanel result = createBasicInnerBagPanel(loadedRulesStr + " - " + projectStr + ": " + project.getName(), false);

        buildInnerElementPanel(result, gbc, CLASS, classTitle);
        buildInnerElementPanel(result, gbc, FIELD, fieldTitle);
        buildInnerElementPanel(result, gbc, METHOD, methodTitle);
        buildInnerElementPanel(result, gbc, PARAMETER, parameterTitle);
        buildInnerElementPanel(result, gbc, LOCALVARIABLE, localVariableTitle);

        return result;
    }

    public void buildInnerElementPanel(@NotNull JPanel result, @NotNull GridBagConstraints gbc,
                                       @NotNull CERuleElement elementRule, @NotNull String panelTitle) {
        JPanel panel = createBasicInnerBagPanel(panelTitle, true);
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = WEST;
        int gridX = 0;
        int gridY = 0;
        for (var entry : features.entrySet()) {
            CERuleFeature feature = entry.getKey();
            CESymbol defaultSymbol = ProjectRuleSymbol.detectDefaultSymbol(feature);
            CESymbol symbol = readRuleEmoji(elementRule, feature, defaultSymbol);
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

    private @Nullable Project getOpenProject() {
        @NotNull Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (openProjects.length > 0) {
            return openProjects[0];
        }
        return null;
    }

    @Contract(" -> new")
    @Override
    public @NotNull CEConfigFile getConfigFile() {
        return new CEConfigFile(getOpenProject());
    }

    @Override
    public Object readConfig(String key) {
        return getConfigFile().getConfigs().get(key);
    }
}
