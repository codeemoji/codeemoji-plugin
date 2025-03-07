package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols;
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
    public LargeMethodCountClassSettings() {
        super(LargeMethodCountClass.class, StructuralAnalysisSymbols.LARGE_METHOD_COUNT_CLASS);
    }

}