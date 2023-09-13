package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.config.CEConfigFile;
import codeemoji.core.collector.config.CERuleElement;
import codeemoji.core.collector.config.CERuleFeature;
import codeemoji.core.collector.project.CEProjectConfigInterface;
import codeemoji.core.collector.project.ProjectRuleSymbol;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.config.CERuleElement.*;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;

@SuppressWarnings("UnstableApiUsage")
public record ShowingSpecificsConfigurable(
        ShowingSpecificsSettings settings) implements ImmediateConfigurable, CEProjectConfigInterface {

    private static @NotNull JPanel createBasicInnerBagPanel(@NotNull final String title, final boolean withBorder) {
        final var result = new JPanel(new GridBagLayout());
        if (withBorder) {
            result.setBorder(BorderFactory.createTitledBorder(title));
        } else {
            result.setBorder(BorderFactory.createTitledBorder(ShowingSpecificsConfigurable.emptyBorder(), title));
        }
        return result;
    }

    @Contract(value = " -> new", pure = true)
    private static @NotNull Border emptyBorder() {
        return BorderFactory.createEmptyBorder(1, 3, 1, 3);
    }

    private static @Nullable Project getOpenProject() {
        @NotNull final Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        if (0 < openProjects.length) {
            return openProjects[0];
        }
        return null;
    }

    @Override
    public @NotNull JComponent createComponent(@NotNull final ChangeListener changeListener) {
        final var specificsPanel = new JPanel();
        final var detailProject = this.initOpenProjectsPanel();
        specificsPanel.add(detailProject);
        return FormBuilder.createFormBuilder()
                .addComponent(specificsPanel)
                .getPanel();
    }

    private @NotNull JComponent initOpenProjectsPanel() {
        final var project = ShowingSpecificsConfigurable.getOpenProject();
        final var file = new CEConfigFile(project);
        if (file.getRules().isEmpty()) {
            return this.howToConfigurePanel();
        }
        if (null != project && !project.isDisposed()) {
            return this.buildPanelsForOpenProject(project);
        } else {
            return new JPanel();
        }
    }

    private @NotNull JComponent howToConfigurePanel() {
        final var panel = new JPanel();
        final var noRuleLoaded = CEBundle.getString("inlay.showingspecifics.options.title.noruleloaded");
        final var howToConfigure = CEBundle.getString("inlay.showingspecifics.options.title.noruleloaded.howtoconfigure");
        panel.add(new JLabel(noRuleLoaded + ":"));
        final var button = new JButton();
        button.setText(new CESymbol(0x1F575).getEmoji() + " " + howToConfigure);
        button.addActionListener(event -> {
            try {
                BrowserUtil.browse(new URI(this.settings().getHowToConfigureURL()));
            } catch (final URISyntaxException ex) {
                CEProjectConfigInterface.LOG.info(ex);
            }
        });
        panel.add(button);
        return panel;
    }

    private @NotNull JPanel buildPanelsForOpenProject(@NotNull final Project project) {
        final var gbc = new GridBagConstraints();
        gbc.fill = HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        final var projectStr = CEBundle.getString("inlay.showingspecifics.options.title.project");
        final var loadedRulesStr = CEBundle.getString("inlay.showingspecifics.options.title.loaded_rules");
        final var classTitle = CEBundle.getString("inlay.showingspecifics.options.title.classes");
        final var fieldTitle = CEBundle.getString("inlay.showingspecifics.options.title.fields");
        final var methodTitle = CEBundle.getString("inlay.showingspecifics.options.title.methods");
        final var parameterTitle = CEBundle.getString("inlay.showingspecifics.options.title.parameters");
        final var localVariableTitle = CEBundle.getString("inlay.showingspecifics.options.title.localvariables");

        final var result = ShowingSpecificsConfigurable.createBasicInnerBagPanel(loadedRulesStr + " - " + projectStr + ": " + project.getName(), false);

        this.buildInnerElementPanel(result, gbc, CLASS, classTitle);
        this.buildInnerElementPanel(result, gbc, FIELD, fieldTitle);
        this.buildInnerElementPanel(result, gbc, METHOD, methodTitle);
        this.buildInnerElementPanel(result, gbc, PARAMETER, parameterTitle);
        this.buildInnerElementPanel(result, gbc, LOCALVARIABLE, localVariableTitle);

        return result;
    }

    public void buildInnerElementPanel(@NotNull final JPanel result, @NotNull final GridBagConstraints gbc,
                                       @NotNull final CERuleElement elementRule, @NotNull final String panelTitle) {
        final var panel = ShowingSpecificsConfigurable.createBasicInnerBagPanel(panelTitle, true);
        final var features = this.readRuleFeatures(elementRule);
        if (!features.isEmpty()) {
            this.buildInnerFeaturePanel(elementRule, features, panel);
            result.add(panel, gbc);
            gbc.gridy++;
        }
    }

    private void buildInnerFeaturePanel(@NotNull final CERuleElement elementRule,
                                        @NotNull final Map<CERuleFeature, List<String>> features,
                                        @NotNull final JPanel panel) {
        final var gbc = new GridBagConstraints();
        gbc.anchor = WEST;
        var gridX = 0;
        var gridY = 0;
        for (final var entry : features.entrySet()) {
            final var feature = entry.getKey();
            final var defaultSymbol = ProjectRuleSymbol.detectDefaultSymbol(feature);
            final var symbol = this.readRuleEmoji(elementRule, feature, defaultSymbol);
            final var key = new JLabel(symbol.getEmoji() + " " + feature.getValue() + ": ");
            var valuesStr = entry.getValue().toString();
            valuesStr = valuesStr.replace("[", "").replace("]", "");
            final var value = new JTextField(valuesStr);
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
        return new CEConfigFile(ShowingSpecificsConfigurable.getOpenProject());
    }

    @SuppressWarnings("unused")
    @Override
    public Object readConfig(final String key) {
        return this.getConfigFile().getConfigs().get(key);
    }
}
