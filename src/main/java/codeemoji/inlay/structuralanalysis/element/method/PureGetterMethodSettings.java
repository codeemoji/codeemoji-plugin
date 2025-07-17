package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.PURE_GETTER_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "PureGetterMethodSettings", storages = @Storage("codeemoji-pure-getter-method-settings.xml"))
public class PureGetterMethodSettings extends CEBaseSettings<PureGetterMethodSettings> {

    private boolean javaBeansNamingConventionApplied = true;

    public PureGetterMethodSettings(){
        super(builder().targetMethods().targetReferences().targetsExternal(),
                PureGetterMethod.class, PURE_GETTER_METHOD);
    }

}
