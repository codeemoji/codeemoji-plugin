package codeemoji.inlay.showingspecifics;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;



@Data
@State(name = "ShowingSpecificsSettings", storages = @Storage("showing-specifics-settings.xml"))
public class ShowingSpecificsSettings implements PersistentStateComponent<ShowingSpecificsSettings> {

    private final String howToConfigureURL;

    public ShowingSpecificsSettings() {
        this.howToConfigureURL ="https://github.com/codeemoji/codeemoji-plugin/tree/develop#cases-of-showing-specifics-of-projects";
    }

    @Override
    public @NotNull ShowingSpecificsSettings getState() {
        return this;
    }

    public void loadState(@NotNull ShowingSpecificsSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}