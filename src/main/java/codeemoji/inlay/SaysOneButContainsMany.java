package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CELocalVariableCollector;
import codeemoji.core.CEMultiProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiTypeElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.CESymbol.MANY;
import static codeemoji.core.CESymbol.SMALL_NAME;

public class SaysOneButContainsMany extends CEMultiProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                  private String[] name;
                }""";
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {
        List<InlayHintsCollector> collectors = new ArrayList<>();

        CEFieldCollector fieldCollector = new CEFieldCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean checkAddInlay(@NotNull PsiField field) {
                PsiTypeElement typeElement = field.getTypeElement();
                return !CEUtil.isPluralForm(field.getName()) &&
                        !CEUtil.sameNameAsType(typeElement, field.getName()) &&
                        (CEUtil.isArrayType(typeElement) || CEUtil.isIterableType(typeElement));
            }
        };

        CELocalVariableCollector localVariableCollector = new CELocalVariableCollector(editor, getKeyId(), SMALL_NAME) {
            @Override
            public boolean checkAddInlay(PsiElement field) {
                return false;
            }
        };

        collectors.add(fieldCollector);
        collectors.add(localVariableCollector);
        return collectors;
    }
}