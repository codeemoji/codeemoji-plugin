package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.CONFUSED;

public class NameContainsOnlySpecialCharacters extends CEProvider<NameContainsOnlySpecialCharacters.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "NameContainsOnlySpecialCharactersSettings", storages = @Storage("codeemoji-name-contains-only-special-characters-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {}

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  private String __;
                       
                  public String getItem(String _____, int ___) {
                      return doSomething(_____, ___);
                  }
                               
                  public Object buildMyObject(int value) {
                      Object ____ = new MyObject();
                      ____.calcData(__, value);
                      return item;
                  }
                }""";
    }

    @Override
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CEVariableCollector(editor, getKey(), CONFUSED) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element){
                return CEUtils.containsOnlySpecialCharacters(Objects.requireNonNull(element.getName()));
            }
        };
    }
}