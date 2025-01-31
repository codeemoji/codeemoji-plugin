package codeemoji.inlay.vcs.recentlymodified;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CESymbolHolder;
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
        super(new CESymbolHolder(
                CEBundle.getString("inlay.recentlymodified.name"),
                CESymbol.of(0x2712)));
    }

}

