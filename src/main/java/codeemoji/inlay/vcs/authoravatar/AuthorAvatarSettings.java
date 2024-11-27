package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.test.CESymbolHolder;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@State(name = "AuthorAvatarSettings", storages = @Storage("codeemoji-author-avatar-settings.xml"))
public final class AuthorAvatarSettings implements PersistentStateComponent<AuthorAvatarSettings> {

    private final List<CESymbolHolder> avatars = new ArrayList<>();
    @Transient
    private transient final Map<String, CESymbol> authorToSymbols = new HashMap<>();  // Map for author-to-emoji string

    public AuthorAvatarSettings() {
    }

    public List<CESymbolHolder> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<CESymbolHolder> avatars) {
        this.avatars.clear();
        this.avatars.addAll(avatars);
        updateDataStructure();
    }

    @Override
    public AuthorAvatarSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AuthorAvatarSettings state) {
        XmlSerializerUtil.copyBean(state, this);
        updateDataStructure();
    }

    private void updateDataStructure() {
        authorToSymbols.clear();
        for (CESymbolHolder pair : avatars) {
            authorToSymbols.put(pair.getName(), pair.getSymbol());
        }
    }

    public CESymbol getSymbolForAuthor(String author) {
        return authorToSymbols.get(author);
    }

}

