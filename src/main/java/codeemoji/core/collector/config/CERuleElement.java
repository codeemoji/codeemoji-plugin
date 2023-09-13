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

    private final String value;

    protected static class EnumDeserializer implements JsonDeserializer<CERuleElement> {
        @Override
        public CERuleElement deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return CERuleElement.valueOf(json.getAsString().toUpperCase());
            } catch (RuntimeException ex) {
                return UNKNOWN;
            }
        }
    }
}
