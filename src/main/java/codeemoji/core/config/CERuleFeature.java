package codeemoji.core.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

@Getter
@AllArgsConstructor
public enum CERuleFeature {
    ANNOTATIONS("annotations"),
    EXTENDS("extends"),
    IMPLEMENTS("implements"),
    TYPES("types"),
    RETURNS("returns"),
    PACKAGES("packages"),
    UNKNOWN("unknown");

    private final @NotNull String value;

    static class EnumDeserializer implements JsonDeserializer<CERuleFeature> {
        @Override
        public @NotNull CERuleFeature deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return valueOf(json.getAsString().toUpperCase());
            } catch (RuntimeException ex) {
                return UNKNOWN;
            }
        }
    }
}
