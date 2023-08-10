package codeemoji.inlay.showingmodifiers;

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
    boolean defaultInterfaceMethod = false;

    @Override
    public ShowingModifiersSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShowingModifiersSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public void change(ShowingModifiers.@NotNull Modifier modifier, boolean value) {
        switch (modifier) {
            //classes
            case PUBLIC_CLASS -> setPublicClass(value);
            case DEFAULT_CLASS -> setDefaultClass(value);
            case FINAL_CLASS -> setFinalClass(value);
            case ABSTRACT_CLASS -> setAbstractClass(value);
            //fields
            case PUBLIC_FIELD -> setPublicField(value);
            case DEFAULT_FIELD -> setDefaultField(value);
            case FINAL_FIELD -> setFinalField(value);
            case PROTECTED_FIELD -> setProtectedField(value);
            case PRIVATE_FIELD -> setPrivateField(value);
            case STATIC_FIELD -> setStaticField(value);
            case VOLATILE_FIELD -> setVolatileField(value);
            case TRANSIENT_FIELD -> setTransientField(value);
            //methods
            case PUBLIC_METHOD -> setPublicMethod(value);
            case DEFAULT_METHOD -> setDefaultMethod(value);
            case FINAL_METHOD -> setFinalMethod(value);
            case PROTECTED_METHOD -> setProtectedMethod(value);
            case PRIVATE_METHOD -> setPrivateMethod(value);
            case STATIC_METHOD -> setStaticMethod(value);
            case ABSTRACT_METHOD -> setAbstractMethod(value);
            case SYNCHRONIZED_METHOD -> setSynchronizedMethod(value);
            case NATIVE_METHOD -> setNativeMethod(value);
            case DEFAULT_INTERFACE_METHOD -> setDefaultInterfaceMethod(value);
        }

    }
}