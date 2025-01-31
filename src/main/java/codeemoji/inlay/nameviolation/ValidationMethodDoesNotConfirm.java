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

public class ValidationMethodDoesNotConfirm extends CEProvider<ValidationMethodDoesNotConfirm.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "ValidationMethodDoesNotConfirm", storages = @Storage("codeemoji-validation-method-does-not-confirm-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {
        public Settings(){
            super(ValidationMethodDoesNotConfirm.class, CONFUSED);
        }
    }

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    private void checkClose() {
                        if(connection.isFinished()){
                            close();
                        }
                    }
                }""";
    }

    @Override
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element){
                return (element.getName().startsWith("validate") || element.getName().startsWith("check") || element.getName().startsWith("ensure")) && !Objects.equals(element.getReturnType(), PsiTypes.booleanType());
            }
        };

    }
}








