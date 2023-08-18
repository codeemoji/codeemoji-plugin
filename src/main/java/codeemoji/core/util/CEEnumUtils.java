package codeemoji.core.util;

import codeemoji.core.collector.project.config.CEEnumWithValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CEEnumUtils {

    private CEEnumUtils() {
    }

    public static <T extends Enum<T>> @Nullable T getEnumByValue(@NotNull Class<T> enumClass, String value) {
        for (T enumValue : enumClass.getEnumConstants()) {
            if (enumValue instanceof CEEnumWithValue valueEnum
                    && valueEnum.getValue().equals(value)) {
                return enumValue;
            }
        }
        return null;
    }
}
