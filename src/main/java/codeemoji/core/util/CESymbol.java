package codeemoji.core.util;

import com.intellij.util.config.Externalizer;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.Attribute;
import lombok.Data;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

// Uff this should be final! Data makes it have setters...
@Data
public final class CESymbol {

    @Attribute(value = "emoji", converter = EmojiToCodePointConverter.class)
    private String emoji;
    @Attribute("hasBackground")
    private boolean withBackground;

    private CESymbol(String emoji, boolean hasBackground) {
        this.emoji = emoji;
        this.withBackground = hasBackground;
    }

    //doesnt work
    static class Serializer implements Externalizer<CESymbol> {

        public Serializer() {
            int aa = 1;
        }

        @Override
        public CESymbol readValue(Element dataElement) {
            return empty();
        }

        @Override
        public void writeValue(Element dataElement, CESymbol value) {
            int aa = 1;
            return;
        }
    }

    public static final CESymbol EMPTY = of(0x26AA);

    public JCheckBox createCheckbox(String name) {
        return createCheckbox(name, false);
    }

    public static CESymbol of(@NotNull String emoji, boolean hasBackground) {
        return new CESymbol(emoji, hasBackground);
    }

    public static CESymbol of(@NotNull String emoji) {
        return of(emoji, true);
    }


    public static CESymbol of(int codePoint, boolean background, @Nullable String suffixText, int... qualifiers) {
        return of(CEParsingUtils.buildEmojiSymbol(codePoint, !background, suffixText, qualifiers), background);
    }

    public static CESymbol empty() {
        return EMPTY;
    }

    //code point oes
    public static CESymbol of(int codePoint) {
        return of(codePoint, 0);
    }

    public static CESymbol of(int codePoint, String suffixText) {
        return of(codePoint, true, suffixText);
    }

    public static CESymbol of(int codePoint, int... qualifiers) {
        return of(codePoint, true, qualifiers);
    }

    public static CESymbol of(int codePoint, boolean background, int... qualifiers) {
        return of(codePoint, background, null, qualifiers);
    }

    public JCheckBox createCheckbox(String name, boolean selected) {
        return new JCheckBox(emoji + " " + name, selected);
    }

    public JLabel createLabel(String name) {
        return new JLabel(" " + emoji + " " + name, SwingConstants.LEFT);
    }

    public void applyToLabel(String name, JLabel label) {
        label.setText(emoji + " " + name);
    }

    public static class EmojiToCodePointConverter extends Converter<String> {

        @Override
        public String fromString(String toDeserialize) {
            // Convert from code points during deserialization
            return CEParsingUtils.parseSymbolFromCodePointString(toDeserialize, true);
        }

        @Override
        public String toString(String toSerialize) {
            // Convert to code points during serialization
            return CEParsingUtils.encodeSymbolToCodePointString(toSerialize);
        }
    }


}
