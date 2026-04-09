package codeemoji.inlay.vcs.refactors.rename;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "NameChangedSettings", storages = @Storage("codeemoji-name-changed-settings.xml"))
public final class NameChangedSettings extends CEBaseSettings<NameChangedSettings> {

    private int maxRevisions = 1;

    public NameChangedSettings() {
        super(builder().targetMethods().targetClasses().targetReferences(),
                NameChanged.class, VCSSymbols.NAME_CHANGED);
    }

}

