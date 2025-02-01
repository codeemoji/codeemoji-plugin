package codeemoji.core.util;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;

@Data
public class CESymbolHolder {

    //TODO. rethink. this should be immutable
    private String id;
    private CESymbol symbol;
    private transient String translatedName;

    public CESymbolHolder(@NotNull CESymbol defaultSymbol, @NotNull String id) {
        this(defaultSymbol, id, new Object[0]);
    }

    //dumb
    public void setId(String id) {
        this.id = id;
        this.translatedName = CEBundle.getString("inlay." + id.toLowerCase(Locale.ROOT) + ".name");
    }

    public CESymbolHolder(@NotNull CESymbol defaultSymbol, @NotNull String name, Object... args) {
        this.symbol = defaultSymbol;
        this.id = name;
        Arrays.stream(args).forEach(arg -> this.id += arg);
        this.translatedName = CEBundle.getString("inlay." + name.toLowerCase(Locale.ROOT) + ".name", args);
    }

    private CESymbolHolder(@NotNull CESymbol symbol, @NotNull String name, @NotNull String translatedName) {
        this.id = name;
        this.symbol = symbol;
        this.translatedName = translatedName;
    }

    public CESymbolHolder() {
    }

    public CESymbolHolder makeCopy() {
        return new CESymbolHolder(symbol, id, translatedName);
    }
}
