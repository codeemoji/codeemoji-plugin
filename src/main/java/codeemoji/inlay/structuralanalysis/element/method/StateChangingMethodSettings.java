package codeemoji.inlay.structuralanalysis.element.method;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@State(name = "StateChangingMethodSettings", storages = @Storage("codeemoji-state-changing-method-settings.xml"))
public class StateChangingMethodSettings implements PersistentStateComponent<StateChangingMethodSettings> {

    private boolean checkMethodCallsForStateChangeApplied = false;

    @Override
    public StateChangingMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull StateChangingMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}