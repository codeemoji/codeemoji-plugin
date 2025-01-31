package codeemoji.inlay.nameviolation;

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
@State(name = "ShortDescriptiveNameSettings", storages = @Storage("codeemoji-short-descriptive-name-settings.xml"))
public class ShortDescriptiveNameSettings extends CEBaseSettings<ShortDescriptiveNameSettings> {

    private int numberOfLetters = 1;

    @Override
    public ShortDescriptiveNameSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShortDescriptiveNameSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}