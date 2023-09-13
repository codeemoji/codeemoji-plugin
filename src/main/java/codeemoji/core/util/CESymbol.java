package codeemoji.core.util;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;

@Data
public class CESymbol {

    private int codePoint = 0x26AA; //white circle
    private int qualifier;
    private boolean background = true;
    private @NotNull String emoji = CESymbol.buildFullEmoji(this.codePoint, this.qualifier, true, null);
    private @Nullable Icon icon;

    public CESymbol() {
    }

    @SuppressWarnings("unused")
    public CESymbol(@Nullable final Icon icon) {
        this.icon = icon;
    }

    public CESymbol(final int codePoint) {
        this.codePoint = codePoint;
        emoji = CESymbol.buildFullEmoji(this.codePoint, qualifier, background, null);
    }

    public CESymbol(final int codePoint, final String suffixText) {
        this.codePoint = codePoint;
        emoji = CESymbol.buildFullEmoji(this.codePoint, qualifier, background, suffixText);
    }

    @SuppressWarnings("unused")
    public CESymbol(final int codePoint, final int qualifier, final boolean background) {
        this.codePoint = codePoint;
        this.qualifier = qualifier;
        this.background = background;
        emoji = CESymbol.buildFullEmoji(this.codePoint, this.qualifier, this.background, null);
    }

    private static @NotNull String buildFullEmoji(final int codePoint, final int qualifier, final boolean addColor, @Nullable final String suffixText) {
        final var codePointChars = Character.toChars(codePoint);
        var withoutColorChars = codePointChars;
        var result = new String(withoutColorChars);
        if (0 < qualifier) {
            final var modifierChars = Character.toChars(qualifier);
            withoutColorChars = Arrays.copyOf(codePointChars, codePointChars.length + modifierChars.length);
            System.arraycopy(modifierChars, 0, withoutColorChars, codePointChars.length, modifierChars.length);
        }
        if (addColor) {
            final var addColorChars = Character.toChars(0x0FE0F);
            final var withColorChars = Arrays.copyOf(withoutColorChars, withoutColorChars.length + addColorChars.length);
            System.arraycopy(addColorChars, 0, withColorChars, withoutColorChars.length, addColorChars.length);
            result = new String(withColorChars);
        }
        if (null != suffixText) {
            result += suffixText;
        }
        return result;
    }
}
