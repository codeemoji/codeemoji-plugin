package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.PsiVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.CONFUSED;

public class NameSuggestsBooleanButTypeDoesNot extends CEProvider<NameSuggestsBooleanButTypeDoesNot.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "NameSuggestsBooleanButTypeDoesNotSettings", storages = @Storage("codeemoji-name-suggests-boolean-but-type-does-not-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {
        public Settings(){
            super(NameSuggestsBooleanButTypeDoesNot.class, CONFUSED);
        }
    }

    @Override
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleVariableCollector(editor, getKey(), mainSymbol()) {
            @Override
            public boolean needsInlay(@NotNull PsiVariable element){
                if (null != element.getName()) {
                    return 2 < element.getName().length() &&
                            element.getName().startsWith("is") && !element.getType().equals(PsiTypes.booleanType());
                }
                return false;
            }
        };
    }
}