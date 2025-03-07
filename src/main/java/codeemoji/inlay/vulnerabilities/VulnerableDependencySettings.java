package codeemoji.inlay.vulnerabilities;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CESymbol;
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

    private CESymbol vulnerableMethod = VulnerableDependencySymbols.VULNERABLE_METHOD;
    private CESymbol vulnerableDependencyCall = VulnerableDependencySymbols.VULNERABLE_DEPENDENCY_CALL;
    private CESymbol indirectVulnerableMethod = VulnerableDependencySymbols.INDIRECT_VULNERABLE_METHOD;
}