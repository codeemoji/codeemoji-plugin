package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.ui.colorpicker.ColorIndicator;
import com.intellij.util.xmlb.annotations.Transient;

import java.awt.*;
import java.util.*;
import java.util.List;

@State(name = "AuthorAvatarSettings", storages = @Storage("codeemoji-author-avatar-settings.xml"))
public final class AuthorAvatarSettings extends CEBaseSettings<AuthorAvatarSettings> {

    private static final List<CESymbol> ANIMALS = List.of(
            CESymbol.of(0x1F981), // Lion Face 🦁
            CESymbol.of(0x1F434), // Horse Face 🐴
            CESymbol.of(0x1F42F), // Tiger Face 🐯
            CESymbol.of(0x1F98A), // Fox Face 🦊
            CESymbol.of(0x1F431), // Cat Face 🐱
            CESymbol.of(0x1F99D), // Raccoon 🦝
            CESymbol.of(0x1F436), // Dog Face 🐶
            CESymbol.of(0x1F42E)  // Cow Face 🐮
    );

    @Transient
    private transient final Map<String, CESymbol> authorToSymbols = new HashMap<>();  // Map for author-to-emoji string

    //save so, we dont add duplicates after reloads
    private int animalIndex = 0;

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

    @Transient
    public CESymbol getNextFriendlyAuthorEmoji() {
        return ANIMALS.get(animalIndex++ % ANIMALS.size());
    }

}

