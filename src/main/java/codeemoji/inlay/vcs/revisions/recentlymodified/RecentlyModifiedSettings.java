package codeemoji.inlay.vcs.revisions.recentlymodified;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "RecentlyModifiedSettings", storages = @Storage("codeemoji-recently-modified-settings.xml"))
public final class RecentlyModifiedSettings extends CEBaseSettings<RecentlyModifiedSettings> {

    private int days = 7;
    private boolean showDate = false;

    public RecentlyModifiedSettings() {
        super(RecentlyModified.class, VCSSymbols.RECENTLY_MODIFIED);
    }

}

