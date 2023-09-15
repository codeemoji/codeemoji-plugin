package codeemoji.core.util;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;

@SuppressWarnings("ConstantValue")
@Data
public class CESymbol {

    private int codePoint = 0x26AA; //white circle
    private int qualifier;
    private boolean background = true;
    private @NotNull String emoji = CESymbol.buildFullEmoji(codePoint, qualifier, true, null);
    private @Nullable Icon icon;

    public CESymbol() {
    }

    @SuppressWarnings("unused")
    public CESymbol(@Nullable Icon icon) {
        this.icon = icon;
    }

    public CESymbol(int codePoint) {
        this.codePoint = codePoint;
        emoji = CESymbol.buildFullEmoji(this.codePoint, qualifier, background, null);
    }

    public CESymbol(int codePoint, String suffixText) {
        this.codePoint = codePoint;
        emoji = CESymbol.buildFullEmoji(this.codePoint, qualifier, background, suffixText);
    }

    @SuppressWarnings("unused")
    public CESymbol(int codePoint, int qualifier, boolean background) {
        this.codePoint = codePoint;
        this.qualifier = qualifier;
        this.background = background;
        emoji = CESymbol.buildFullEmoji(this.codePoint, this.qualifier, this.background, null);
    }

    private static @NotNull String buildFullEmoji(int codePoint, int qualifier, boolean addColor, @Nullable String suffixText) {
        var codePointChars = Character.toChars(codePoint);
        var withoutColorChars = codePointChars;
        var result = new String(withoutColorChars);
        if (0 < qualifier) {
            var modifierChars = Character.toChars(qualifier);
            withoutColorChars = Arrays.copyOf(codePointChars, codePointChars.length + modifierChars.length);
            System.arraycopy(modifierChars, 0, withoutColorChars, codePointChars.length, modifierChars.length);
        }
        if (addColor) {
            var addColorChars = Character.toChars(0x0FE0F);
            var withColorChars = Arrays.copyOf(withoutColorChars, withoutColorChars.length + addColorChars.length);
            System.arraycopy(addColorChars, 0, withColorChars, withoutColorChars.length, addColorChars.length);
            result = new String(withColorChars);
        }
        if (null != suffixText) {
            result += suffixText;
        }
        return result;
    }
}
