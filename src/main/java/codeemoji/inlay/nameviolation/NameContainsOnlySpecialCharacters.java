package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.CONFUSED;

@SuppressWarnings("UnstableApiUsage")
public class NameContainsOnlySpecialCharacters extends CEProvider<NoSettings> {

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
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEVariableCollector(editor, getKey(), CONFUSED) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element){
                return CEUtils.containsOnlySpecialCharacters(Objects.requireNonNull(element.getName()));
            }
        };
    }
}