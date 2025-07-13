package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "LargeMethodCountClassSettings", storages = @Storage("codeemoji-large-method-count-class-settings.xml"))
public class LargeMethodCountClassSettings extends CEBaseSettings<LargeMethodCountClassSettings> {

    private int methodCount = 15;
    public LargeMethodCountClassSettings() {
        super(CEPSIType.METHODS, 
                LargeMethodCountClass.class, StructuralAnalysisSymbols.LARGE_METHOD_COUNT_CLASS);
    }

}