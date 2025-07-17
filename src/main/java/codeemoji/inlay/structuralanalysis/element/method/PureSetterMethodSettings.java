package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.PURE_SETTER_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "PureSetterMethodSettings", storages = @Storage("codeemoji-pure-setter-method-settings.xml"))
public class PureSetterMethodSettings extends CEBaseSettings<PureSetterMethodSettings> {

    private boolean javaBeansNamingConventionApplied = true;

    public PureSetterMethodSettings(){
        super(builder().targetMethods().targetReferences().targetsExternal(),
                PureSetterMethod.class, PURE_SETTER_METHOD);
    }

}
