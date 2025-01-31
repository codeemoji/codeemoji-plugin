package codeemoji.inlay.vulnerabilities;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "VulnerableMethodsSettings", storages = @Storage("codeemoji-vulnerable-methods-settings.xml"))
public class VulnerableDependencySettings extends CEBaseSettings<VulnerableDependencySettings> {

    private boolean checkVulnerableDependencyApplied = true;

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