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
    boolean publicClass = true;
    boolean abstractClass = true;
    boolean finalClass = true;
    boolean strictFPClass = true;
    boolean defaultClass = true;
    // fields
    boolean publicField = true;
    boolean protectedField = true;
    boolean defaultField = true;
    boolean privateField = true;
    boolean finalField = true;
    boolean staticField = true;
    boolean transientField = true;
    boolean volatileField = true;
    boolean nativeField = true;
    // methods
    boolean publicMethod = true;
    boolean protectedMethod = true;
    boolean defaultMethod = true;
    boolean privateMethod = true;
    boolean abstractMethod = true;
    boolean synchronizedMethod = true;
    boolean nativeMethod = true;
    boolean strictFPMethod = true;
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