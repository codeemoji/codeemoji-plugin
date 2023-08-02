package codeemoji.core;

import lombok.Data;

@Data
public class CESymbol {

    private int codePoint = 0x2757;
    private int modifier = 0;
    private boolean background = true;

    public CESymbol() {
    }

    public CESymbol(int codePoint) {
        this.codePoint = codePoint;
    }

    public CESymbol(int codePoint, int modifier, boolean background) {
        this.codePoint = codePoint;
        this.modifier = modifier;
        this.background = background;
    }
}
