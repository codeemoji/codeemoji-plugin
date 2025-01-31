package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "LargeMethodCountClassSettings", storages = @Storage("codeemoji-large-method-count-class-settings.xml"))
public class LargeMethodCountClassSettings extends CEBaseSettings<LargeMethodCountClassSettings> {

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