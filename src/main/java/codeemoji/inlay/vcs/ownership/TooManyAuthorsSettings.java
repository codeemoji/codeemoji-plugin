package codeemoji.inlay.vcs.ownership;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "TooManyAuthors", storages = @Storage("codeemoji-too-many-authors-settings.xml"))
public final class TooManyAuthorsSettings extends CEBaseSettings<TooManyAuthorsSettings> {

    private int minimumAuthors = 5;

    public TooManyAuthorsSettings() {
        super(TooManyAuthors.class, VCSSymbols.TOO_MANY_OWNERS);
    }

}
