package state;

import com.intellij.openapi.application.ApplicationManager;
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

    public static BadVariableNameState getInstance() {
        return ApplicationManager.getApplication().getService(BadVariableNameState.class);
    }

    @Nullable
    @Override
    public BadVariableNameState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull BadVariableNameState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public void noStateLoaded() {
        PersistentStateComponent.super.noStateLoaded();
    }

}