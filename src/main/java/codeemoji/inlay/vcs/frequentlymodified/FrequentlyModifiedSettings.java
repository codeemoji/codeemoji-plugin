package codeemoji.inlay.vcs.frequentlymodified;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.inlay.vcs.VCSSymbols;
import codeemoji.inlay.vcs.recentlymodified.RecentlyModified;
import com.intellij.codeInsight.hints.InlayHintsProvider;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "RecentlyModifiedSettings", storages = @Storage("codeemoji-recently-modified-settings.xml"))
public final class FrequentlyModifiedSettings extends CEBaseSettings<FrequentlyModifiedSettings> {

    private int days = 7;
    private boolean showDate = false;
    public FrequentlyModifiedSettings() {
        super(RecentlyModified.class, VCSSymbols.RECENTLY_MODIFIED);
    }

}

