package codeemoji.inlay.vcs.recentlymodified;

import codeemoji.core.util.CESymbol;
import codeemoji.core.base.CEBaseSettings;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "RecentlyModifiedSettings", storages = @Storage("codeemoji-recently-modified-settings.xml"))
public final class RecentlyModifiedSettings extends CEBaseSettings<RecentlyModifiedSettings> {

    private int days = 7;
    public RecentlyModifiedSettings() {
        super(new CESymbolHolder("Recently Modified", CESymbol.of(0x1F4C5)));
    }

}

