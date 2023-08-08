package codeemoji.inlay.nameviolation;

import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import codeemoji.core.CEVariableCollector;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CEConstants.CONFUSED;

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
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), CONFUSED) {
            @Override
            public boolean isHintable(@NotNull PsiVariable element) {
                return CEUtil.containsOnlySpecialCharacters(element.getName());
            }
        };
    }
}