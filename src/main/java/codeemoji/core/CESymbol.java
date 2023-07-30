package codeemoji.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CESymbol {
    DEFAULT(0x2757),
    COLOR_BACKGROUND(0x0FE0F),
    SMALL_NAME(0x1F90F),
    CONFUSED(0x1F937),
    MANY(0x1F590),
    ONE(0x261D);

    private final int value;

}
