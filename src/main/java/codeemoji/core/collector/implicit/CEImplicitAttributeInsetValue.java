package codeemoji.core.collector.implicit;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class CEImplicitAttributeInsetValue {

    private @Nullable
    final Object value;
    private final int shiftOffset;

    public CEImplicitAttributeInsetValue(@Nullable Object value) {
        this.value = value;
        shiftOffset = 1;
    }

    public CEImplicitAttributeInsetValue(@Nullable Object value, int shiftOffset) {
        this.value = value;
        this.shiftOffset = shiftOffset;
    }
}
