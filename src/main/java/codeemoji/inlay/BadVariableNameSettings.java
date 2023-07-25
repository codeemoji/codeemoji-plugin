package codeemoji.inlay;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "BadVariableNameSettings", storages = @Storage("bad-variable-name-settings.xml"))
public class BadVariableNameSettings implements PersistentStateComponent<BadVariableNameSettings> {

    private Integer numberOfLetters = 1;

    @Nullable
    @Override
    public BadVariableNameSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BadVariableNameSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}