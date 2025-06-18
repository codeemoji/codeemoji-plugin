package codeemoji.inlay.vcs.revisions.newlyadded;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "NewlyAddedSettings", storages = @Storage("codeemoji-newly-added-settings.xml"))
public final class NewlyAddedSettings extends CEBaseSettings<NewlyAddedSettings> {

    public NewlyAddedSettings() {
        super(NewlyAdded.class, VCSSymbols.NEWLY_ADDED);
    }

}

