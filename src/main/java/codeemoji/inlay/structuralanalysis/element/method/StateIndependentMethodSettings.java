package codeemoji.inlay.structuralanalysis.element.method;


import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.STATE_INDEPENDENT_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "StateIndependentMethodSettings", storages = @Storage("codeemoji-state-independent-method-settings.xml"))
public class StateIndependentMethodSettings extends CEBaseSettings<StateIndependentMethodSettings> {

    private boolean checkMethodCallsForStateIndependenceApplied = false;

    public StateIndependentMethodSettings(){
        super(builder().targetMethods().targetReferences().targetsExternal(),
                StateIndependentMethod.class, STATE_INDEPENDENT_METHOD);
    }

}
