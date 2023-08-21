package codeemoji.inlay.showingspecifics;

import codeemoji.core.collector.project.CEOpenProject;
import codeemoji.core.collector.project.config.CEProjectConfigFile;
import codeemoji.core.collector.project.config.CEProjectRule;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

public record ShowingSpecificsConfigurable(ShowingSpecificsSettings settings) implements ImmediateConfigurable {

    @Override
    public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
        var specificsPanel = new JPanel();

        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        ComboBox<CEOpenProject> comboBox = new ComboBox<>();
        //comboBox.addItem(new Project("", null));
        for (Project project : openProjects) {
            //comboBox.addItem(new CEOpenProject(project.getName(), project));
        }

        for (Project project : openProjects) {
            CEProjectConfigFile configFile = new CEProjectConfigFile(project);
            List<CEProjectRule> projectRules = configFile.getProjectRules();
            for (CEProjectRule rule : projectRules) {
                System.out.println(rule);
            }
        }

        specificsPanel.add(comboBox);

        return FormBuilder.createFormBuilder()
                .addComponent(specificsPanel)
                .getPanel();
    }

}
