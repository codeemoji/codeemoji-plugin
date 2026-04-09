package codeemoji.core.config;

import codeemoji.inlay.external.VulnerabilityInfo;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@State(name = "CEGlobalSettings", storages = @Storage("codeemoji-global-settings.xml"))
public class CEGlobalSettings implements PersistentStateComponent<CEGlobalSettings> {

    private Boolean myExternalServiceState = true;

    private Boolean useSecondaryVulnerabilityScanner = false;

    private String ossApiToken = "";

    private boolean frontEmojiPlacement = false;

    public static CEGlobalSettings getInstance() {
        return ApplicationManager.getApplication().getService(CEGlobalSettings.class);
    }

    public VulnerabilityInfo.ScannerType getScannerType() {
        if (useSecondaryVulnerabilityScanner) {
            return VulnerabilityInfo.ScannerType.OSS;
        }
        else {
            return VulnerabilityInfo.ScannerType.OSV;
        }

    }

    @NotNull
    @Override
    public CEGlobalSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CEGlobalSettings state) {
        myExternalServiceState = state.myExternalServiceState;
        useSecondaryVulnerabilityScanner = state.useSecondaryVulnerabilityScanner;
        ossApiToken = state.ossApiToken;
        frontEmojiPlacement = state.frontEmojiPlacement;
    }

    public void fireSettingsChanged() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : openProjects) {
            DaemonCodeAnalyzer.getInstance(project).restart();
        }
    }



}
