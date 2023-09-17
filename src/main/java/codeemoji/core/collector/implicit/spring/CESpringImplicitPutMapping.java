package codeemoji.core.collector.implicit.spring;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CESpringImplicitPutMapping extends CESpringImplicitMapping {

    private final @NotNull String baseName;

    public CESpringImplicitPutMapping() {
        baseName = "org.springframework.web.bind.annotation.PutMapping";
    }

}