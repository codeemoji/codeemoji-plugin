package codeemoji.core;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@Data
public class CESymbol {

    private int codePoint = 0x2757;
    private int modifier = 0;
    private boolean background = true;
    private Icon icon = null;

    public CESymbol() {
    }

    public CESymbol(@Nullable Icon icon) {
        try {
            this.icon = icon;
        } catch (RuntimeException ignored) {
        }
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
