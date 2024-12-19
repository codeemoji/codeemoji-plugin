package codeemoji.core.util;

import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.util.config.Externalizer;
import com.intellij.util.xmlb.Converter;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public abstract class CESymbol {

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

    public static final Utf EMPTY = of(0x26AA);

    public JCheckBox createCheckbox(String name) {
        return createCheckbox(name, false);
    }

    public abstract JCheckBox createCheckbox(String name, boolean selected);

    public abstract JLabel createLabel(String name);

    public abstract void applyToLabel(String name, JLabel label);

    public abstract InlayPresentation createPresentation(PresentationFactory factory, boolean small);

    public InlayPresentation createPresentation(PresentationFactory factory) {
        return createPresentation(factory, true);
    }

    //icons
    public static Ico of(@NotNull Icon icon, boolean hasBackground) {
        return new Ico(icon, true);
    }

    public static Ico of(@NotNull Icon icon) {
        return of(icon, true);
    }

    //string ones

    public static Utf of(@NotNull String emoji, boolean hasBackground) {
        return new Utf(emoji, hasBackground);
    }

    public static Utf of(@NotNull String emoji) {
        return of(emoji, true);
    }


    public static Utf of(int codePoint, boolean background, @Nullable String suffixText, int... qualifiers) {
        return of(CEParsingUtils.buildEmojiSymbol(codePoint, !background, suffixText, qualifiers), background);
    }

    public static Utf empty() {
        return EMPTY;
    }

    //code point oes
    public static Utf of(int codePoint) {
        return of(codePoint, 0);
    }

    public static Utf of(int codePoint, String suffixText) {
        return of(codePoint, true, suffixText);
    }

    public static Utf of(int codePoint, int... qualifiers) {
        return of(codePoint, true, qualifiers);
    }

    public static Utf of(int codePoint, boolean background, int... qualifiers) {
        return of(codePoint, background, null, qualifiers);
    }

    //this should be a record...
    //It's supposed to be immutable
    @EqualsAndHashCode(callSuper = true)
    @Tag("Utf")
    @Data
    public static class Utf extends CESymbol {
        @Attribute(value = "emoji", converter = EmojiToCodePointConverter.class)
        private String emoji;
        @Attribute("hasBackground")
        private boolean hasBackground;

        public Utf() {
        }

        public Utf(String emoji, boolean hasBackground) {
            this.emoji = emoji;
            this.hasBackground = hasBackground;
        }

        @Override
        public JCheckBox createCheckbox(String name, boolean selected) {
            return new JCheckBox(emoji + " " + name, selected);
        }

        @Override
        public JLabel createLabel(String name) {
            return new JLabel(" " + emoji + " " + name, SwingConstants.LEFT);
        }

        @Override
        public void applyToLabel(String name, JLabel label) {
            label.setText(emoji + " " + name);
        }

        @Override
        public InlayPresentation createPresentation(PresentationFactory factory, boolean small) {
            return small ? factory.smallText(emoji) : factory.text(emoji);
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

    @Data
    @Tag("Ico")
    public static class Ico extends CESymbol {

        @Attribute("icon")
        private Icon icon;
        @Attribute("hasBackground")
        private boolean hasBackground;

        public Ico() {
        }

        public Ico(Icon icon, boolean hasBackground) {
            this.icon = icon;
            this.hasBackground = hasBackground;
        }

        @Override
        public JCheckBox createCheckbox(String name, boolean selected) {
            return new JCheckBox(name, icon, selected);
        }

        @Override
        public JLabel createLabel(String name) {
            return new JLabel(name, icon, SwingConstants.LEFT);
        }

        @Override
        public void applyToLabel(String name, JLabel label) {
            label.setIcon(icon);
            label.setName(name);
        }

        @Override
        public InlayPresentation createPresentation(PresentationFactory factory, boolean small) {
            return small ? factory.smallScaledIcon(icon) : factory.icon(icon);
        }
    }

}
