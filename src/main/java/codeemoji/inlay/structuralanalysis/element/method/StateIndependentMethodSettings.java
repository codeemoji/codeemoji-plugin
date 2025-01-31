package codeemoji.inlay.structuralanalysis.element.method;


import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.STATE_INDEPENDENT_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "StateIndependentMethodSettings", storages = @Storage("codeemoji-state-independent-method-settings.xml"))
public class StateIndependentMethodSettings extends CEBaseSettings<StateIndependentMethodSettings> {

    private boolean checkMethodCallsForStateIndependenceApplied = false;

    public StateIndependentMethodSettings(){
        super(StateIndependentMethod.class, STATE_INDEPENDENT_METHOD);
    }
    @Override
    public StateIndependentMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull StateIndependentMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
