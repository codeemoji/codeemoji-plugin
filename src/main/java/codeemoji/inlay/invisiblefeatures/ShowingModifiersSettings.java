package codeemoji.inlay.invisiblefeatures;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@State(name = "ShowingModifiersSettings", storages = @Storage("showing-modifiers-settings.xml"))
public class ShowingModifiersSettings implements PersistentStateComponent<ShowingModifiersSettings> {
    //classes
    boolean publicClass = false;
    boolean abstractClass = false;
    boolean finalClass = false;
    boolean defaultClass = false;
    // fields
    boolean publicField = false;
    boolean protectedField = false;
    boolean defaultField = false;
    boolean privateField = false;
    boolean finalField = false;
    boolean staticField = false;
    boolean transientField = true;
    boolean volatileField = true;
    // methods
    boolean publicMethod = false;
    boolean protectedMethod = false;
    boolean defaultMethod = false;
    boolean privateMethod = false;
    boolean staticMethod = false;
    boolean finalMethod = false;
    boolean abstractMethod = false;
    boolean synchronizedMethod = true;
    boolean nativeMethod = true;
    boolean defaultInterfaceMethod = true;

    @Override
    public ShowingModifiersSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShowingModifiersSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}