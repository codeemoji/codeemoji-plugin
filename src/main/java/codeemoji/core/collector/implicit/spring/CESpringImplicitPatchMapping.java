package codeemoji.core.collector.implicit.spring;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class CESpringImplicitPatchMapping extends CESpringImplicitMapping {

    private final @NotNull String baseName;

    public CESpringImplicitPatchMapping() {
        baseName = "org.springframework.web.bind.annotation.PatchMapping";
    }

}