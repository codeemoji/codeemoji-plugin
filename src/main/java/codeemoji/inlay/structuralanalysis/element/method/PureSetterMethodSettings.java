package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.PURE_SETTER_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "PureSetterMethodSettings", storages = @Storage("codeemoji-pure-setter-method-settings.xml"))
public class PureSetterMethodSettings extends CEBaseSettings<PureSetterMethodSettings> {

    private boolean javaBeansNamingConventionApplied = true;

    public PureSetterMethodSettings(){
        super(PureSetterMethod.class, PURE_SETTER_METHOD);
    }
    @Override
    public @Nullable PureSetterMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PureSetterMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
