package codeemoji.core.util;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;

@Data
public class CESymbol {

    private int codePoint = 0x26AA; //white circle
    private int qualifier = 0;
    private boolean background = true;
    private String emoji = buildEmoji(codePoint, 0, true);
    private Icon icon = null;

    public CESymbol() {
    }

    @SuppressWarnings("unused")
    public CESymbol(@Nullable Icon icon) {
        this.icon = icon;
    }

    public CESymbol(int codePoint) {
        this.codePoint = codePoint;
        this.emoji = buildEmoji(getCodePoint(), getQualifier(), isBackground());
    }

    @SuppressWarnings("unused")
    public CESymbol(int codePoint, int qualifier, boolean background) {
        this.codePoint = codePoint;
        this.qualifier = qualifier;
        this.background = background;
        this.emoji = buildEmoji(getCodePoint(), getQualifier(), isBackground());
    }

    private static @NotNull String buildEmoji(int codePoint, int qualifier, boolean addColor) {
        var codePointChars = Character.toChars(codePoint);
        var withoutColorChars = codePointChars;
        if (qualifier > 0) {
            var modifierChars = Character.toChars(qualifier);
            withoutColorChars = Arrays.copyOf(codePointChars, codePointChars.length + modifierChars.length);
            System.arraycopy(modifierChars, 0, withoutColorChars, codePointChars.length, modifierChars.length);
        }
        if (addColor) {
            var addColorChars = Character.toChars(0x0FE0F);
            var withColorChars = Arrays.copyOf(withoutColorChars, withoutColorChars.length + addColorChars.length);
            System.arraycopy(addColorChars, 0, withColorChars, withoutColorChars.length, addColorChars.length);
            return new String(withColorChars);
        }
        return new String(withoutColorChars);
    }
}
