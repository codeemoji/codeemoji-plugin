package codeemoji.inlay.vcs.revisions.unfrequentlymodified;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "UnFrequentlyModifiedSettings", storages = @Storage("codeemoji-unfrequently-modified-settings.xml"))
public final class UnFrequentlyModifiedSettings extends CEBaseSettings<UnFrequentlyModifiedSettings> {

    private int days = 360;
    private boolean showDate = false;

    public UnFrequentlyModifiedSettings() {
        super(CEPSIType.METHODS_AND_CLASSES, 
                UnFrequentlyModified.class, VCSSymbols.UN_FREQUENTLY_MODIFIED);
    }

}

