package codeemoji.core.collector.config;

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
    UNKNOWN("unknown");

    private final @NotNull String value;

    static class EnumDeserializer implements JsonDeserializer<CERuleFeature> {
        @Override
        public @NotNull CERuleFeature deserialize(@NotNull final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            try {
                return CERuleFeature.valueOf(json.getAsString().toUpperCase());
            } catch (final RuntimeException ex) {
                return CERuleFeature.UNKNOWN;
            }
        }
    }
}
