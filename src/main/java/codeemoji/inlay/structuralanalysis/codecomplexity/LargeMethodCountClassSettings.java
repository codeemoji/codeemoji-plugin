package codeemoji.inlay.structuralanalysis.codecomplexity;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "LargeMethodCountClassSettings", storages = @Storage("codeemoji-large-method-count-class-settings.xml"))
public class LargeMethodCountClassSettings implements PersistentStateComponent<LargeMethodCountClassSettings> {

    private int methodCount = 15;

    @Override
    public @Nullable LargeMethodCountClassSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LargeMethodCountClassSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}