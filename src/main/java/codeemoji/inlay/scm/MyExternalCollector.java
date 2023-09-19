package codeemoji.inlay.scm;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.util.CESymbol;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class MyExternalCollector extends CEMethodCollector {

    protected MyExternalCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
        return false;
    }

}