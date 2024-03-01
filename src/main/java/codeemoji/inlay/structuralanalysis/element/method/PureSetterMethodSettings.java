package codeemoji.inlay.structuralanalysis.element.method;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "PureSetterMethodSettings", storages = @Storage("codeemoji-pure-setter-method-settings.xml"))
public class PureSetterMethodSettings implements PersistentStateComponent<PureSetterMethodSettings> {

    private boolean javaBeansNamingConventionApplied = true;

    @Override
    public @Nullable PureSetterMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PureSetterMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
