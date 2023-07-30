package codeemoji.core;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CESymbol.DEFAULT;

@Data
public class CEInlay {

    private int codePoint = DEFAULT.getValue();
    private int modifier = 0;
    private boolean background = true;

    public CEInlay() {
    }

    public CEInlay(@NotNull CESymbol ceSymbol) {
        this.codePoint = ceSymbol.getValue();
    }

    public CEInlay(int codePoint) {
        this.codePoint = codePoint;
    }

    public CEInlay(int codePoint, int modifier, boolean background) {
        this.codePoint = codePoint;
        this.modifier = modifier;
        this.background = background;
    }
}
