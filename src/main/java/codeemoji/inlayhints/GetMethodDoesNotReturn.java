package codeemoji.inlayhints;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GetMethodDoesNotReturn extends CEProvider<NoSettings> {

    @Override
    public @Nullable String getPreviewText() {
        return """
                public class Customer {
                    public void getName() {
                        doSomething();
                    }
                }""";
    }

    @Override
    public InlayHintsCollector getCollector(@NotNull Editor editor) {
        return new CEMethodCollector(editor) {
            @Override
            public void processInlayHint(@Nullable PsiMethod method, InlayHintsSink sink) {
                InlayPresentation inlay = configureInlayHint(getName(), 0x1F937, true);
                if ((method != null &&
                        method.getName().startsWith("get")) &&
                        Objects.equals(method.getReturnType(), PsiTypes.voidType())) {
                    addInlayHint(method, sink, inlay);
                }
            }
        };
    }
}








