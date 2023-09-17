package codeemoji.core.collector.implicit.spring;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CESpringImplicitDeleteMapping extends CESpringImplicitMapping {

    private final @NotNull String baseName;

    public CESpringImplicitDeleteMapping() {
        baseName = "org.springframework.web.bind.annotation.DeleteMapping";
    }

}