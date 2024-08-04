package codeemoji.core.config;

import codeemoji.inlay.external.VulnerabilityInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@State(name = "CEGlobalSettings", storages = @Storage("codeemoji-global-settings.xml"))
public class CEGlobalSettings implements PersistentStateComponent<CEGlobalSettings> {

    private Boolean myExternalServiceState = true;

    private VulnerabilityInfo.ScannerType scannerType = VulnerabilityInfo.ScannerType.OSS;

    public static CEGlobalSettings getInstance() {
        return ApplicationManager.getApplication().getService(CEGlobalSettings.class);
    }

    public VulnerabilityInfo.ScannerType getType() {
        return this.scannerType;
    }

    @NotNull
    @Override
    public CEGlobalSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CEGlobalSettings state) {
        myExternalServiceState = state.myExternalServiceState;
    }

}
