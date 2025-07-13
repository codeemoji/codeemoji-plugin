package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.LARGE_LINE_COUNT_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "LargeLineCountMethodSettings", storages = @Storage("codeemoji-large-line-count-method-settings.xml"))
public class LargeLineCountMethodSettings extends CEBaseSettings<LargeLineCountMethodSettings> {

    private int linesOfCode = 20;
    private boolean commentExclusionApplied = false;

    public LargeLineCountMethodSettings(){
        super(CEPSIType.METHODS, 
                LargeLineCountMethod.class, LARGE_LINE_COUNT_METHOD);
    }

}
