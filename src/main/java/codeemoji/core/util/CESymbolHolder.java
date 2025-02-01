package codeemoji.core.util;

import com.intellij.util.xmlb.annotations.Transient;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

@Data
public class CESymbolHolder {

    private String id;
    private CESymbol symbol;

    private @Transient String translatedName;

    public CESymbolHolder(@NotNull CESymbol defaultSymbol, @NotNull String id) {
        this.symbol = defaultSymbol;
        this.id = id;
    }

    public void setId(String id) {
        this.id = id.toLowerCase(Locale.ROOT);
        this.translatedName = null;
    }

    @Transient
    public String getTranslatedName() {
        if (translatedName == null) {
            translatedName = CEBundle.getOptional("inlay." + id.toLowerCase(Locale.ROOT) + ".name", id);
        }
        return translatedName;
    }

    public CESymbolHolder(@NotNull CESymbol defaultSymbol, @NotNull String translationKey, Object... args) {
        this.symbol = defaultSymbol;
        this.id = translationKey;
        Arrays.stream(args).forEach(arg -> this.id += arg);
        this.translatedName = CEBundle.getString(translationKey, args);
    }

    private CESymbolHolder(@NotNull CESymbol symbol, @NotNull String name, @NotNull String translatedName) {
        this.id = name;
        this.symbol = symbol;
        this.translatedName = translatedName;
    }

    public CESymbolHolder() {
    }

    public CESymbolHolder makeCopy() {
        return new CESymbolHolder(symbol, id, getTranslatedName());
    }
}
