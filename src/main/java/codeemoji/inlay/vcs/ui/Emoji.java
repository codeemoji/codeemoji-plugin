package codeemoji.inlay.vcs.ui;

import org.jetbrains.annotations.NotNull;

public record Emoji(String symbol, String category, String description) {
    @Override
    public String toString() {
        return String.format("%1s %2s", symbol, description);
    }


    // Converts Unicode code points (e.g., "1F610") to an emoji character, ensuring color rendering
    public static String parseSymbolFromCodePoints(@NotNull String input, boolean addColor) {
        String[] codePoints = input.split(" "); // Split the input string by spaces

        StringBuilder emojiBuilder = new StringBuilder();

        for (String codePoint : codePoints) {
            // Skip FE0F and skin tone modifiers (U+1F3FB to U+1F3FF)
            if (codePoint.isEmpty() || codePoint.equals("FE0F") || (codePoint.compareTo("1F3FB") >= 0 && codePoint.compareTo("1F3FF") <= 0)) {
                continue;
            }

            // Append the code point to the emoji builder
            emojiBuilder.appendCodePoint(Integer.parseInt(codePoint, 16));

            // If addColor is true, append FE0F (variation selector) unless it's 200D
            if (addColor && !codePoint.equals("200D")) {
                emojiBuilder.appendCodePoint(0xFE0F); // Append the color variation selector
            }
        }

        return emojiBuilder.toString();
    }


    public static String symbolToCodePoints(String input) {
        StringBuilder codePointsBuilder = new StringBuilder();

        // Convert each code point in the input string
        input.codePoints().forEach(codePoint -> {
            if (!codePointsBuilder.isEmpty()) {
                codePointsBuilder.append(" "); // Separate code points with a space
            }
            codePointsBuilder.append(Integer.toHexString(codePoint).toUpperCase()); // Convert to uppercase hexadecimal
        });

        return codePointsBuilder.toString();
    }
}
