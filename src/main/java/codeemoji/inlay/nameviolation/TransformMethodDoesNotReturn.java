package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.CONFUSED;

public class TransformMethodDoesNotReturn extends CEProvider<TransformMethodDoesNotReturn.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "TransformMethodDoesNotReturn", storages = @Storage("codeemoji-transform-method-does-not-return-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {}

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
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), CONFUSED) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element){
                return (element.getName().startsWith("translate") || element.getName().startsWith("transform") || element.getName().startsWith("convert")) && Objects.equals(element.getReturnType(), PsiTypes.voidType());
            }
        };
    }
}








