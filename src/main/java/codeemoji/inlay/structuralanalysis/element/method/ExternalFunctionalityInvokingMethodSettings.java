package codeemoji.inlay.structuralanalysis.element.method;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@State(name = "ExternalFunctionalityInvokingMethodSettings", storages = @Storage("codeemoji-external-functionality-invoking-method-settings.xml"))
public class ExternalFunctionalityInvokingMethodSettings implements PersistentStateComponent<ExternalFunctionalityInvokingMethodSettings> {

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