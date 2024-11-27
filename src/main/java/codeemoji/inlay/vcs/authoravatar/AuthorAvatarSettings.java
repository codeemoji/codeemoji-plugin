package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.ui.Emoji;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@State(name = "AuthorAvatarSettings", storages = @Storage("codeemoji-author-avatar-settings.xml"))
public final class AuthorAvatarSettings implements PersistentStateComponent<AuthorAvatarSettings> {

    private List<Pair> avatars = new ArrayList<>();
    @Transient
    private transient final Map<String, CESymbol> authorToSymbols = new HashMap<>();  // Map for author-to-emoji string
    public AuthorAvatarSettings() {
    }

    public void setAvatars(List<Pair> avatars) {
        this.avatars = avatars;
        markDirty();
    }

    @Override
    public AuthorAvatarSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AuthorAvatarSettings state) {
        XmlSerializerUtil.copyBean(state, this);

        if (avatars == null) {
            avatars = new ArrayList<>();
        }
    }

    public void markDirty() {
        authorToSymbols.clear();
        for (Pair pair : avatars) {
            authorToSymbols.put(pair.getAuthor(), CESymbol.of(pair.getSymbol()));
        }
    }

    public CESymbol getSymbolForAuthor(String author) {
        return authorToSymbols.get(author);
    }

    @Getter @Setter
    public static class Pair {
        private String author = "";
        private String emojiCodePoints = ""; // Store code points instead of the raw emoji

        @Transient
        public String getSymbol() {
            return Emoji.parseSymbolFromCodePoints(emojiCodePoints, true); // Convert to emoji symbol
        }

        @Transient
        public void setSymbol(String symbol) {
            emojiCodePoints = Emoji.symbolToCodePoints(symbol); // Store code points
        }
    }
}

