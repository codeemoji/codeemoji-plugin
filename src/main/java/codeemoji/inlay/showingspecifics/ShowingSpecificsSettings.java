package codeemoji.inlay.showingspecifics;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode
@State(name = "ShowingSpecificsSettings", storages = @Storage("showing-specifics-settings.xml"))
public class ShowingSpecificsSettings implements PersistentStateComponent<ShowingSpecificsSettings> {

    @Override
    public @NotNull ShowingSpecificsSettings getState() {
        return this;
    }

    public void loadState(@NotNull ShowingSpecificsSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}