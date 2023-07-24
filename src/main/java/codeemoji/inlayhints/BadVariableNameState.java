package codeemoji.inlayhints;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "BadVariableNameHintState", storages = @Storage("bad-variable-name-hint.xml"))
public class BadVariableNameState implements PersistentStateComponent<BadVariableNameState> {

    private Integer numberOfLetters = 1;

    @Nullable
    @Override
    public BadVariableNameState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BadVariableNameState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}