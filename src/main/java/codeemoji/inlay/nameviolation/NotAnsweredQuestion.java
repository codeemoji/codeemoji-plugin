package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEMethodCollector;
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
public class NotAnsweredQuestion extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public void isHuman() {
                        doSomething();
                    }
                }""";
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEMethodCollector(editor, getKey(), CONFUSED) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                return element.getName().startsWith("is") && Objects.equals(element.getReturnType(), PsiTypes.voidType());
            }
        };

    }
}








