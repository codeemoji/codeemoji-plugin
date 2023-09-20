package codeemoji.inlay.implicit;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode
@State(name = "ImplicitAnnotationsSettings", storages = @Storage("codeemoji-implicit-annotations-settings.xml"))
public class ImplicitAnnotationsSettings implements PersistentStateComponent<ImplicitAnnotationsSettings> {

    @Override
    public @NotNull ImplicitAnnotationsSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ImplicitAnnotationsSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}