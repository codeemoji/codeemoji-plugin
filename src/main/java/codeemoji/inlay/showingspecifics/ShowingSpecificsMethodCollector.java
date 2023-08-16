package codeemoji.inlay.showingspecifics;

import codeemoji.core.CEClassCollector;
import codeemoji.core.config.CEConfigFile;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

public class ShowingSpecificsMethodCollector extends CEClassCollector {

    private final CEConfigFile configFile;

    public ShowingSpecificsMethodCollector(@NotNull Editor editor, @NotNull String keyId, CEConfigFile configFile) {
        super(editor, keyId + ".method", null);
        this.configFile = configFile;
    }

    @Override
    public boolean isHintable(@NotNull PsiClass element) {
        return true;
    }
}