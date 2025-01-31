package codeemoji.core.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Data
@Setter
@Getter
public class CESymbolHolder {

    //TODO. rethink. this should be immutable
    private String name;
    private CESymbol symbol;

    public CESymbolHolder(@NotNull String name, @NotNull CESymbol defaultSymbol) {
        this.name = name;
        this.symbol = defaultSymbol;
    }

    public CESymbolHolder() {
    }

    public CESymbolHolder makeCopy() {
        return new CESymbolHolder(name, symbol);
    }
}
