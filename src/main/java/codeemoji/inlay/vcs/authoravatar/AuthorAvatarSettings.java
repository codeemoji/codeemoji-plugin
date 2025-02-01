package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Transient;

import java.util.*;

@State(name = "AuthorAvatarSettings", storages = @Storage("codeemoji-author-avatar-settings.xml"))
public final class AuthorAvatarSettings extends CEBaseSettings<AuthorAvatarSettings> {

    @Transient
    private transient final Map<String, CESymbol> authorToSymbols = new HashMap<>();  // Map for author-to-emoji string

    public AuthorAvatarSettings() {
        onUpdated();
    }

    @Override
    public void onUpdated() {
        authorToSymbols.clear();
        for (CESymbolHolder pair : getSymbols()) {
            authorToSymbols.put(pair.getId(), pair.getSymbol());
        }
    }

    public CESymbol getSymbolForAuthor(String author) {
        return authorToSymbols.get(author);
    }

}

