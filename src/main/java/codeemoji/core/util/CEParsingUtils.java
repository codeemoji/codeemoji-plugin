package codeemoji.core.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for parsing and constructing emoji symbols using Unicode code points.
 */
public final class CEParsingUtils {

    private CEParsingUtils() {
        // Utility class: private constructor to prevent instantiation
    }

    private static final int ZERO_WIDTH_JOINER = 0x200D;
    private static final int COLOR_VARIATION_SELECTOR = 0xFE0F;
    private static final int[] SKIN_TONE_MODIFIERS = {0x1F3FB, 0x1F3FC, 0x1F3FD, 0x1F3FE, 0x1F3FF};

    /**
     * Constructs an emoji symbol using a base character and optional extra components.
     *
     * @param baseChar     the base Unicode code point
     * @param blackAndWhite whether to omit color variation selectors
     * @param extra         additional string to append after the emoji (nullable)
     * @param extraChars    additional code points to join with the base character
     * @return the constructed emoji symbol as a string
     */
    public static @NotNull String buildEmojiSymbol(int baseChar, boolean blackAndWhite,
                                                   @Nullable String extra, int... extraChars) {
        StringBuilder emojiBuilder = new StringBuilder();

        // Include the base character and additional code points
        int[] allCodePoints = new int[extraChars.length + 1];
        allCodePoints[0] = baseChar;
        System.arraycopy(extraChars, 0, allCodePoints, 1, extraChars.length);
        for (int codePoint : allCodePoints) {
            if (!emojiBuilder.isEmpty()) {
                emojiBuilder.appendCodePoint(ZERO_WIDTH_JOINER);
            }
            emojiBuilder.appendCodePoint(codePoint);
            if (!blackAndWhite) {
                emojiBuilder.appendCodePoint(COLOR_VARIATION_SELECTOR);
            }
        }

        if (extraChars.length > 3) {

            int aa = 1;
        }
        // Append additional text, if specified
        if (extra != null) {
            emojiBuilder.append(extra);
        }

        return emojiBuilder.toString();
    }

    /**
     * Converts a space-separated string of Unicode code points to an emoji character.
     *
     * @param input    the space-separated code points (e.g., "1F600 1F3FB")
     * @param addColor whether to append color variation selectors
     * @return the constructed emoji character
     */
    public static @NotNull String parseSymbolFromCodePointString(@NotNull String input, boolean addColor) {
        String[] codePointsHex = input.trim().split("\\s+");
        StringBuilder emojiBuilder = new StringBuilder();

        for (String codePointHex : codePointsHex) {
            if (codePointHex.isEmpty()) {
                continue;
            }

            int codePoint;
            try {
                codePoint = Integer.parseInt(codePointHex, 16);
            } catch (NumberFormatException e) {
                // Skip invalid code points
                continue;
            }

            if (isSkinToneModifier(codePoint) || codePoint == COLOR_VARIATION_SELECTOR) {
                continue;
            }

            emojiBuilder.appendCodePoint(codePoint);

            // Append color variation selector if requested, unless the code point is ZWJ
            if (addColor && codePoint != ZERO_WIDTH_JOINER) {
                emojiBuilder.appendCodePoint(COLOR_VARIATION_SELECTOR);
            }
        }

        return emojiBuilder.toString();
    }

    /**
     * Converts an emoji string into a space-separated string of its Unicode code points.
     *
     * @param input the emoji string
     * @return the space-separated Unicode code points in uppercase hexadecimal
     */
    public static @NotNull String encodeSymbolToCodePointString(@NotNull String input) {
        StringBuilder codePointsBuilder = new StringBuilder();

        input.codePoints().forEach(codePoint -> {
            if (!codePointsBuilder.isEmpty()) {
                codePointsBuilder.append(" ");
            }
            codePointsBuilder.append(Integer.toHexString(codePoint).toUpperCase());
        });

        return codePointsBuilder.toString();
    }

    // Helper Methods

    private static boolean isSkinToneModifier(int codePoint) {
        for (int modifier : SKIN_TONE_MODIFIERS) {
            if (codePoint == modifier) {
                return true;
            }
        }
        return false;
    }
}
