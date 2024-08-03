package codeemoji.inlay.vulnerabilities;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@State(name = "VulnerableMethodsSettings", storages = @Storage("codeemoji-vulnerable-methods-settings.xml"))
public class VulnerableDependencySettings implements PersistentStateComponent<VulnerableDependencySettings> {

    private boolean checkVulnerableDependencyApplied = false;

    @Override
    public VulnerableDependencySettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull VulnerableDependencySettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean isCheckVulnerableDependecyApplied() {
        return checkVulnerableDependencyApplied;
    }

    public void setCheckVulnerableDependecyApplied(boolean selected) {
        checkVulnerableDependencyApplied = selected;
    }
}