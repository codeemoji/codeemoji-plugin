package codeemoji.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CEElementRule implements CEEnumWithValue {
    CLASS("class"), FIELD("field"), METHOD("method"), LOCALVARIABLE("localvariable");
    private final String value;
}
