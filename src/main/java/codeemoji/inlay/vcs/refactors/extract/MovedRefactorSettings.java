package codeemoji.inlay.vcs.refactors.extract;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "MovedRefactorSettings", storages = @Storage("codeemoji-moved-refactor-settings.xml"))
public final class MovedRefactorSettings extends CEBaseSettings<MovedRefactorSettings> {

    private int maxRevisions = 1;

    public MovedRefactorSettings() {
        super(builder().targetMethods().targetClasses().targetReferences(),
                MovedRefactor.class, VCSSymbols.MOVED_REFACTOR);
    }

}

