package codeemoji.inlay;

import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import codeemoji.core.CEVariableCollector;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.core.CEConstants.ONE;

public class SaysManyButContainsOne extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  private String names;
                  //...
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), ONE) {
            @Override
            public boolean isHintable(@NotNull PsiVariable element) {
                PsiTypeElement typeElement = element.getTypeElement();
                return typeElement != null &&
                        CEUtil.isPluralForm(element.getName()) &&
                        CEUtil.isNotGenericType(typeElement) &&
                        CEUtil.isNotNumericType(typeElement) &&
                        !CEUtil.isArrayType(typeElement) &&
                        !CEUtil.isIterableType(typeElement) &&
                        !CEUtil.containsOnlySpecialCharacters(typeElement.getText());
            }
        };
    }
}