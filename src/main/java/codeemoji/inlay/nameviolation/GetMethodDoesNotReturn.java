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
import static codeemoji.inlay.nameviolation.NameViolationSymbols.MANY;

public class GetMethodDoesNotReturn extends CEProvider<GetMethodDoesNotReturn.Settings> {
    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "GetMethodDoesNotReturnSettings", storages = @Storage("codeemoji-get-method-does-not-return-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {
        public Settings() {
            super(GetMethodDoesNotReturn.class, CONFUSED);
        }
    }

    @Override
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
            @Override
            public boolean needsInlay(@NotNull PsiMethod element){
                return (element.getName().startsWith("get") || element.getName().startsWith("return")) && Objects.equals(element.getReturnType(), PsiTypes.voidType());
            }
        };
    }
}








