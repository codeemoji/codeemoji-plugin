package codeemoji.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CEFeatureRule implements CEEnumWithValue {
    ANNOTATIONS("annotations"), EXTENDS("extends"), IMPLEMENTS("implements"), TYPES("types"), RETURNS("returns");
    private final String value;
}
