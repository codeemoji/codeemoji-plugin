package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.CONFUSED;

@SuppressWarnings("UnstableApiUsage")
public class TransformMethodDoesNotReturn extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public void translateText(String text) {
                        text = doSomething(text);
                    }
                }""";
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), CONFUSED) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element){
                return (element.getName().startsWith("translate") || element.getName().startsWith("transform") || element.getName().startsWith("convert")) && Objects.equals(element.getReturnType(), PsiTypes.voidType());
            }
        };
    }
}








