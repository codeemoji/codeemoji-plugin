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

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.LARGE_LINE_COUNT_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "LargeLineCountMethodSettings", storages = @Storage("codeemoji-large-line-count-method-settings.xml"))
public class LargeLineCountMethodSettings extends CEBaseSettings<LargeLineCountMethodSettings> {

    private int linesOfCode = 20;
    private boolean commentExclusionApplied = false;

    public LargeLineCountMethodSettings(){
        super(LargeLineCountMethod.class, LARGE_LINE_COUNT_METHOD);
    }
    @Override
    public @Nullable LargeLineCountMethodSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LargeLineCountMethodSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
