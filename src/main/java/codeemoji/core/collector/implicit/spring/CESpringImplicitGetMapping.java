package codeemoji.core.collector.implicit.spring;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CESpringImplicitGetMapping extends CESpringImplicitMapping {

    private final @NotNull String baseName;

    public CESpringImplicitGetMapping() {
        baseName = "org.springframework.web.bind.annotation.GetMapping";
    }

}