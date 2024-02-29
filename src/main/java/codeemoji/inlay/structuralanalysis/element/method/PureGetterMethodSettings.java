package codeemoji.inlay.structuralanalysis.element.method;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@State(name = "PureGetterMethodSettings", storages = @Storage("codeemoji-pure-getter-method-settings.xml"))
public class PureGetterMethodSettings implements PersistentStateComponent<PureGetterMethodSettings> {

    private boolean javaBeansNamingConventionApplied = true;

    @Override
    public PureGetterMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PureGetterMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
