package codeemoji.inlay.showingmodifiers;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier;
import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.*;

@ToString
@EqualsAndHashCode
@State(name = "ShowingModifiersSettings", storages = @Storage("codeemoji-showing-modifiers-settings.xml"))
public class ShowingModifiersSettings implements PersistentStateComponent<ShowingModifiersSettings> {

    @MapAnnotation
    private final Map<ScopeModifier, Boolean> basicModifiersMap = new EnumMap<>(ScopeModifier.class);

    public ShowingModifiersSettings() {
        basicModifiersMap.put(VOLATILE_FIELD, true);
        basicModifiersMap.put(TRANSIENT_FIELD, true);
        basicModifiersMap.put(SYNCHRONIZED_METHOD, true);
        basicModifiersMap.put(NATIVE_METHOD, true);
    }

    @Override
    public @NotNull ShowingModifiersSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShowingModifiersSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public synchronized boolean query(@NotNull ScopeModifier scopeModifier) {
        basicModifiersMap.putIfAbsent(scopeModifier, false);
        return basicModifiersMap.get(scopeModifier);
    }

    public synchronized void update(@NotNull ScopeModifier scopeModifier, boolean value) {
        basicModifiersMap.put(scopeModifier, value);
    }

}