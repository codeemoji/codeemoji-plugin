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
    private @NotNull String emoji = buildFullEmoji(codePoint, qualifier, true, null);
    private @Nullable Icon icon = null;

    public CESymbol() {
    }

    @SuppressWarnings("unused")
    public CESymbol(@Nullable Icon icon) {
        this.icon = icon;
    }

    public CESymbol(int codePoint) {
        this.codePoint = codePoint;
        this.emoji = buildFullEmoji(getCodePoint(), getQualifier(), isBackground(), null);
    }

    public CESymbol(int codePoint, String suffixText) {
        this.codePoint = codePoint;
        this.emoji = buildFullEmoji(getCodePoint(), getQualifier(), isBackground(), suffixText);
    }

    @SuppressWarnings("unused")
    public CESymbol(int codePoint, int qualifier, boolean background) {
        this.codePoint = codePoint;
        this.qualifier = qualifier;
        this.background = background;
        this.emoji = buildFullEmoji(getCodePoint(), getQualifier(), isBackground(), null);
    }

    private static @NotNull String buildFullEmoji(int codePoint, int qualifier, boolean addColor, @Nullable String suffixText) {
        var codePointChars = Character.toChars(codePoint);
        var withoutColorChars = codePointChars;
        var result = new String(withoutColorChars);
        if (qualifier > 0) {
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
        if (suffixText != null) {
            result += suffixText;
        }
        return result;
    }
}
