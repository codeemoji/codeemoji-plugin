package codeemoji.inlay.vcs.lastcommit;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "LastCommitSettings", storages = @Storage("codeemoji-last-commit-settings.xml"))
public final class LastCommitSettings extends CEBaseSettings<LastCommitSettings> {
    public LastCommitSettings() {
        super(LastCommit.class, VCSSymbols.LAST_COMMIT);
    }

}

