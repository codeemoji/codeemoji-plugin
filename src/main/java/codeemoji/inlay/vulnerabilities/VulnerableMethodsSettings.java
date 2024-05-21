package codeemoji.inlay.vulnerabilities;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@State(name = "VulnerableMethodsSettings", storages = @Storage("codeemoji-vulnerable-methods-settings.xml"))
public class VulnerableMethodsSettings implements PersistentStateComponent<codeemoji.inlay.vulnerabilities.VulnerableMethodsSettings> {

    private boolean checkMethodCallsForExternalityApplied = false;

    @Override
    public codeemoji.inlay.vulnerabilities.VulnerableMethodsSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull codeemoji.inlay.vulnerabilities.VulnerableMethodsSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}