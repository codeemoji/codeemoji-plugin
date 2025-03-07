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
@State(name = "FrequentlyModifiedSettings", storages = @Storage("codeemoji-frequently-modified-settings.xml"))
public final class FrequentlyModifiedSettings extends CEBaseSettings<FrequentlyModifiedSettings> {

    private int modifications = 15;
    private int daysTimeFrame = 30;
    public FrequentlyModifiedSettings() {
        super(FrequentlyModified.class, VCSSymbols.FREQUENTLY_MODIFIED);
    }

}

