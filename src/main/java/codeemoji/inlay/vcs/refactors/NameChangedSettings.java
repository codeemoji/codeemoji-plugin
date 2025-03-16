package codeemoji.inlay.vcs.refactors;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import codeemoji.inlay.vcs.lastcommit.LastCommit;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "NameChangedSettings", storages = @Storage("codeemoji-name-changed-settings.xml"))
public final class NameChangedSettings extends CEBaseSettings<NameChangedSettings> {

    public NameChangedSettings() {
        super(NameChanged.class, VCSSymbols.NAME_CHANGED);
    }

}

