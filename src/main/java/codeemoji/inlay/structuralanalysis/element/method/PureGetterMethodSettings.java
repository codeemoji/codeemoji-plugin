package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.PURE_GETTER_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "PureGetterMethodSettings", storages = @Storage("codeemoji-pure-getter-method-settings.xml"))
public class PureGetterMethodSettings extends CEBaseSettings<PureGetterMethodSettings> {

    private boolean javaBeansNamingConventionApplied = true;

    public PureGetterMethodSettings(){
        super(PureGetterMethod.class, PURE_GETTER_METHOD);
    }

}
