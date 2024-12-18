package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.base.CEBaseSettings;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@State(name = "AuthorAvatarSettings", storages = @Storage("codeemoji-author-avatar-settings.xml"))
public final class AuthorAvatarSettings extends CEBaseSettings<AuthorAvatarSettings> {

    @Transient
    private transient final Map<String, CESymbol> authorToSymbols = new HashMap<>();  // Map for author-to-emoji string

    public AuthorAvatarSettings() {
    }

    @Override
    public void loadState(@NotNull AuthorAvatarSettings state) {
        super.loadState(state);
        updateDataStructure();
    }

    @Override
    public void setSymbols(ArrayList<CESymbolHolder> copy) {
        super.setSymbols(copy);
        updateDataStructure();
    }

    private void updateDataStructure() {
        authorToSymbols.clear();
        for (CESymbolHolder pair : getSymbols()) {
            authorToSymbols.put(pair.getName(), pair.getSymbol());
        }
    }

    public CESymbol getSymbolForAuthor(String author) {
        return authorToSymbols.get(author);
    }

}

