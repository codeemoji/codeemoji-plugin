package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.LARGE_IDENTIFIER_COUNT_METHOD;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "LargeIdentifierCountMethodSettings", storages = @Storage("codeemoji-large-identifier-count-method-settings.xml"))
public class LargeIdentifierCountMethodSettings extends CEBaseSettings<LargeIdentifierCountMethodSettings> {

    private int identifierCount = 70;

    public LargeIdentifierCountMethodSettings(){
        super(builder().targetMethods().targetReferences(),
                LargeIdentifierCountMethod.class, LARGE_IDENTIFIER_COUNT_METHOD);
    }
}
