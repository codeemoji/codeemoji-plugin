package codeemoji.inlay.showingmodifiers;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier;
import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.NATIVE_METHOD;
import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.SYNCHRONIZED_METHOD;
import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.TRANSIENT_FIELD;
import static codeemoji.inlay.showingmodifiers.ShowingModifiers.ScopeModifier.VOLATILE_FIELD;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@State(name = "ShowingModifiersSettings", storages = @Storage("codeemoji-showing-modifiers-settings.xml"))
public class ShowingModifiersSettings extends CEBaseSettings<ShowingModifiersSettings> {

    @MapAnnotation
    private final Map<ScopeModifier, Boolean> basicModifiersMap = new EnumMap<>(ScopeModifier.class);

    // hacky. capitalized since keyword is reserved
    private CESymbol Public = ShowingModifiersSymbols.PUBLIC_SYMBOL;
    private CESymbol Default = ShowingModifiersSymbols.DEFAULT_SYMBOL;
    private CESymbol Final = ShowingModifiersSymbols.FINAL_SYMBOL;
    private CESymbol FinalVar = ShowingModifiersSymbols.FINAL_VAR_SYMBOL;
    private CESymbol Protected = ShowingModifiersSymbols.PROTECTED_SYMBOL;
    private CESymbol Private = ShowingModifiersSymbols.PRIVATE_SYMBOL;
    private CESymbol Static = ShowingModifiersSymbols.STATIC_SYMBOL;
    private CESymbol Abstract = ShowingModifiersSymbols.ABSTRACT_SYMBOL;
    private CESymbol Synchronized = ShowingModifiersSymbols.SYNCHRONIZED_SYMBOL;
    private CESymbol Native = ShowingModifiersSymbols.NATIVE_SYMBOL;
    private CESymbol DefaultInterface = ShowingModifiersSymbols.DEFAULT_INTERFACE_SYMBOL;
    private CESymbol Volatile = ShowingModifiersSymbols.VOLATILE_SYMBOL;
    private CESymbol Transient = ShowingModifiersSymbols.TRANSIENT_SYMBOL;


    public ShowingModifiersSettings() {
        basicModifiersMap.put(VOLATILE_FIELD, true);
        basicModifiersMap.put(TRANSIENT_FIELD, true);
        basicModifiersMap.put(SYNCHRONIZED_METHOD, true);
        basicModifiersMap.put(NATIVE_METHOD, true);
    }

    synchronized boolean query(@NotNull ScopeModifier scopeModifier) {
        basicModifiersMap.putIfAbsent(scopeModifier, false);
        return basicModifiersMap.get(scopeModifier);
    }

    synchronized void update(@NotNull ScopeModifier scopeModifier, boolean value) {
        basicModifiersMap.put(scopeModifier, value);
    }

}