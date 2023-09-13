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
public enum CERuleElement {
    CLASS("class"),
    FIELD("field"),
    METHOD("method"),
    PARAMETER("parameter"),
    LOCALVARIABLE("localvariable"),
    UNKNOWN("unknown");

    private final @NotNull String value;

    protected static class EnumDeserializer implements JsonDeserializer<CERuleElement> {
        @Override
        public @NotNull CERuleElement deserialize(@NotNull final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            try {
                return CERuleElement.valueOf(json.getAsString().toUpperCase());
            } catch (final RuntimeException ex) {
                return CERuleElement.UNKNOWN;
            }
        }
    }
}
