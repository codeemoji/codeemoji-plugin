package codeemoji.inlay.structuralanalysis.element.method;

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
@State(name = "ExternalFunctionalityInvokingMethodSettings", storages = @Storage("codeemoji-external-functionality-invoking-method-settings.xml"))
public class ExternalFunctionalityInvokingMethodSettings extends CEBaseSettings<ExternalFunctionalityInvokingMethodSettings> {

    private boolean checkMethodCallsForExternalityApplied = false;

    @Override
    public ExternalFunctionalityInvokingMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ExternalFunctionalityInvokingMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}