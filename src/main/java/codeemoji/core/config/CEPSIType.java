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
public enum CEPSIType {
    METHODS("declaration"),
    CLASSES("references"),
    METHODS_AND_CLASSES("methods_and_classes"),
    UNSPECIFIED("unspecified");

    private final @NotNull String value;

    static class EnumDeserializer implements JsonDeserializer<CEPSIType> {
        @Override
        public @NotNull CEPSIType deserialize(@NotNull JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return valueOf(json.getAsString().toUpperCase());
            } catch (RuntimeException ex) {
                return METHODS;
            }
        }
    }

    public boolean isMethods() {
        return this == METHODS || this == METHODS_AND_CLASSES;
    }

    public boolean isClasses() {
        return this == CLASSES || this == METHODS_AND_CLASSES;
    }
}
