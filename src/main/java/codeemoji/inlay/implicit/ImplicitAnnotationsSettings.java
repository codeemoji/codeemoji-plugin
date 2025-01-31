package codeemoji.inlay.implicit;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@State(name = "ImplicitAnnotationsSettings", storages = @Storage("codeemoji-implicit-annotations-settings.xml"))
public class ImplicitAnnotationsSettings extends CEBaseSettings<ImplicitAnnotationsSettings> {

    @Override
    public @NotNull ImplicitAnnotationsSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ImplicitAnnotationsSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}