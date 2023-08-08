package codeemoji.inlay.nameviolation;

import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import codeemoji.core.CEVariableCollector;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CEConstants.MANY;

public class SaysOneButContainsMany extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                import java.util.*;
                                
                public class Customer {
                  private String[] name;
                  
                  public String getItem(byte[] buffer, List device) {
                    return doSomething(buffer, device);
                  }
                  
                  public List<Object> transformValue(int value) {
                    List<Object> item = new ArrayList<>();
                    item.addAll(doSomething(name, value));
                    return item;
                  }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean isHintable(@NotNull PsiVariable element) {
                PsiTypeElement typeElement = element.getTypeElement();
                return !CEUtil.isPluralForm(element.getName()) &&
                        !CEUtil.sameNameAsType(typeElement, element.getName()) &&
                        (CEUtil.isArrayType(typeElement) || CEUtil.isIterableType(typeElement));
            }

        };
    }
}
