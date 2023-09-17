package codeemoji.core.collector.implicit.spring;

import com.intellij.openapi.editor.Editor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CESpringRestControllerCollector extends CESpringControllerCollector {

    public final @NotNull String baseName;
    private final List<String> deactivatedCases = new ArrayList<>();

    public CESpringRestControllerCollector(@NotNull Editor editor, @NotNull String keyId, int codePoint) {
        super(editor, keyId, codePoint);
        deactivatedCases.add("org.springframework.stereotype.Controller");
        baseName = "org.springframework.web.bind.annotation.RestController";
    }
}
