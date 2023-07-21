package my;

import codemoji.CodeemojiCollector;
import codemoji.CodeemojiProvider;
import com.intellij.codeInsight.hints.*;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GetMethodDoesNotReturn extends CodeemojiProvider {

    @Override
    public InlayHintsCollector getCollector(Editor editor) {
        return new CodeemojiCollector(editor) {
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








