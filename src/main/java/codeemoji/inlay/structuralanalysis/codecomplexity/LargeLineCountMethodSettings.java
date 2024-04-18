package codeemoji.inlay.structuralanalysis.codecomplexity;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "LargeLineCountMethodSettings", storages = @Storage("codeemoji-large-line-count-method-settings.xml"))
public class LargeLineCountMethodSettings implements PersistentStateComponent<LargeLineCountMethodSettings> {

    private int linesOfCode = 20;
    private boolean commentExclusionApplied = false;

    @Override
    public @Nullable LargeLineCountMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LargeLineCountMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
