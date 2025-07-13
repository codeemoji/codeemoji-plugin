package codeemoji.inlay.vcs.bugs;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "FixedIssue", storages = @Storage("codeemoji-fixed-issue-settings.xml"))
public final class FixedIssueSettings extends CEBaseSettings<FixedIssueSettings> {

    private int maxRevisions = 1;

    public FixedIssueSettings() {
        super(CEPSIType.METHODS_AND_CLASSES, 
                FixedIssue.class, VCSSymbols.FIXES_BUG);
    }

}
