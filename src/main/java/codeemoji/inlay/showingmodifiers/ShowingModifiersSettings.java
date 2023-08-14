package codeemoji.inlay.showingmodifiers;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.XMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

@ToString
@EqualsAndHashCode
@State(name = "ShowingModifiersSettings", storages = @Storage("showing-modifiers-settings.xml"))
public class ShowingModifiersSettings implements PersistentStateComponent<ShowingModifiersSettings> {

    @XMap
    private final HashMap<ShowingModifiers.Modifier, Boolean> basicModifiersMap = new HashMap<>();

    public ShowingModifiersSettings() {
        basicModifiersMap.put(ShowingModifiers.Modifier.VOLATILE_FIELD, true);
        basicModifiersMap.put(ShowingModifiers.Modifier.TRANSIENT_FIELD, true);
        basicModifiersMap.put(ShowingModifiers.Modifier.SYNCHRONIZED_METHOD, true);
        basicModifiersMap.put(ShowingModifiers.Modifier.NATIVE_METHOD, true);
    }

    @Override
    public @NotNull ShowingModifiersSettings getState() {
        return this;
    }

    public void loadState(@NotNull ShowingModifiersSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public synchronized boolean query(@NotNull ShowingModifiers.Modifier modifier) {
        return Objects.requireNonNullElse(basicModifiersMap.get(modifier), false);
    }

    public synchronized void update(@NotNull ShowingModifiers.Modifier modifier, boolean value) {
        basicModifiersMap.put(modifier, value);
    }

}