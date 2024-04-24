package codeemoji.inlay.structuralanalysis.codecomplexity;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "HighCyclomaticComplexityMethodSettings", storages = @Storage("codeemoji-high-cyclomatic-complexity-method-settings.xml"))
public class HighCyclomaticComplexityMethodSettings implements PersistentStateComponent<HighCyclomaticComplexityMethodSettings> {

    private int cyclomaticComplexityThreshold = 1;
    private int lineCountStartThreshold = 1;
    private double cyclomaticComplexityPerLine = 0.36;

    @Override
    public @Nullable HighCyclomaticComplexityMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull HighCyclomaticComplexityMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
