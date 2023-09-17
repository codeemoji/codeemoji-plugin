package codeemoji.core.collector.implicit.spring;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CESpringImplicitPostMapping extends CESpringImplicitMapping {

    private final @NotNull String baseName;

    public CESpringImplicitPostMapping() {
        baseName = "org.springframework.web.bind.annotation.PostMapping";
    }

}