package codeemoji.inlay;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.core.CESymbol.CONFUSED;

public class TransformMethodDoesNotReturn extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public void translateText(String text) {
                        doSomething();
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), CONFUSED) {
            @Override
            public boolean putHintHere(@NotNull PsiMethod element) {
                return (element.getName().startsWith("translate") || element.getName().startsWith("transform") || element.getName().startsWith("convert")) && Objects.equals(element.getReturnType(), PsiTypes.voidType());
            }
        };
    }
}








