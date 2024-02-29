package codeemoji.inlay.structuralanalysis.element.method;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@State(name = "StateIndependentMethodSettings", storages = @Storage("codeemoji-state-independent-method-settings.xml"))
public class StateIndependentMethodSettings implements PersistentStateComponent<StateIndependentMethodSettings> {

    private boolean checkMethodCallsForStateIndependenceApplied = false;

    @Override
    public StateIndependentMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull StateIndependentMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
