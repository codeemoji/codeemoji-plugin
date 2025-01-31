package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.STATE_CHANGING_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "StateChangingMethodSettings", storages = @Storage("codeemoji-state-changing-method-settings.xml"))
public class StateChangingMethodSettings extends CEBaseSettings<StateChangingMethodSettings> {

    private boolean checkMethodCallsForStateChangeApplied = false;

    public StateChangingMethodSettings(){
        super(StateChangingMethod.class, STATE_CHANGING_METHOD);
    }
    @Override
    public StateChangingMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull StateChangingMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}