package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.HIGH_CYCLOMATIC_COMPLEXITY_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "HighCyclomaticComplexityMethodSettings", storages = @Storage("codeemoji-high-cyclomatic-complexity-method-settings.xml"))
public class HighCyclomaticComplexityMethodSettings extends CEBaseSettings<HighCyclomaticComplexityMethodSettings> {

    private int cyclomaticComplexityThreshold = 1;
    private int lineCountStartThreshold = 1;
    private double cyclomaticComplexityPerLine = 0.36;

    public HighCyclomaticComplexityMethodSettings(){
        super(builder().targetMethods().targetReferences(),
                HighCyclomaticComplexityMethod.class, HIGH_CYCLOMATIC_COMPLEXITY_METHOD);
    }

}
