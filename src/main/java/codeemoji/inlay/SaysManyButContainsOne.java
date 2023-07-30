package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiTypeElement;

import static codeemoji.core.CESymbol.ONE;

public class SaysManyButContainsOne extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  private String names;
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEFieldCollector(editor, getKey().getId(), ONE) {
            @Override
            public boolean checkAddInlay(PsiField field) {
                PsiTypeElement typeElement = field.getTypeElement();
                return typeElement != null &&
                        CEUtil.isPluralForm(field.getName()) &&
                        CEUtil.isNotGenericType(typeElement) &&
                        CEUtil.isNotNumericType(typeElement) &&
                        !CEUtil.isArrayType(typeElement) &&
                        !CEUtil.isIterableType(typeElement) &&
                        !CEUtil.containsOnlySpecialCharacters(typeElement.getText());
            }
        };
    }
}