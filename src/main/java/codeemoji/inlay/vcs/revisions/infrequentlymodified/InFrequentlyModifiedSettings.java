package codeemoji.inlay.vcs.revisions.infrequentlymodified;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "InFrequentlyModifiedSettings", storages = @Storage("codeemoji-infrequently-modified-settings.xml"))
public final class InFrequentlyModifiedSettings extends CEBaseSettings<InFrequentlyModifiedSettings> {

    private int days = 360;
    private boolean showDate = false;

    public InFrequentlyModifiedSettings() {
        super(builder().targetMethods(false).targetClasses().targetReferences(),
                InFrequentlyModified.class, VCSSymbols.UN_FREQUENTLY_MODIFIED);
    }

}

