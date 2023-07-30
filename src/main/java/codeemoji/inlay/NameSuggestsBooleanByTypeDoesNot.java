package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiTypes;

import static codeemoji.core.CESymbol.CONFUSED;

public class NameSuggestsBooleanByTypeDoesNot extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  private int isPattern;
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEFieldCollector(editor, getKey().getId(), CONFUSED) {
            @Override
            public boolean checkAddInlay(PsiField field) {
                return field != null && (field.getName().startsWith("is") && !field.getType().equals(PsiTypes.booleanType()));
            }
        };
    }
}