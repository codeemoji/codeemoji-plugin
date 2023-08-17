package codeemoji.core.collector.project.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CEElementRule implements CEEnumWithValue {
    CLASS("class"), FIELD("field"), METHOD("method"), PARAMETER("parameter"), LOCALVARIABLE("localvariable");
    private final String value;
}
