package codeemoji.core.util;

import codeemoji.core.enums.CEEnumWithValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CEEnumUtils {
    public static <T extends Enum<T>> @Nullable T getEnumByValue(@NotNull Class<T> enumClass, String value) {
        for (T enumValue : enumClass.getEnumConstants()) {
            if (enumValue instanceof CEEnumWithValue valueEnum) {
                if (valueEnum.getValue().equals(value)) {
                    return enumValue;
                }
            }
        }
        return null;
    }
}
