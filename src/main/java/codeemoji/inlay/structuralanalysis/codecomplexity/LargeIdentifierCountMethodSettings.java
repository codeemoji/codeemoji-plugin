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
@State(name = "LargeIdentifierCountMethodSettings", storages = @Storage("codeemoji-large-identifier-count-method-settings.xml"))
public class LargeIdentifierCountMethodSettings extends CEBaseSettings<LargeIdentifierCountMethodSettings> {

    private int identifierCount = 70;

    @Override
    public @Nullable LargeIdentifierCountMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LargeIdentifierCountMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
