package codeemoji.inlay.showingspecifics;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "ShowingSpecificsSettings", storages = @Storage("codeemoji-showing-specifics-settings.xml"))
public class ShowingSpecificsSettings extends CEBaseSettings<ShowingSpecificsSettings> {

    private final @NotNull String howToConfigureURL;

    public ShowingSpecificsSettings() {
        howToConfigureURL = "https://github.com/codeemoji/codeemoji-plugin/tree/develop#cases-of-showing-specifics-of-projects";
    }
}