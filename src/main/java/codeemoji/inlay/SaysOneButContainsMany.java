package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CELocalVariableCollector;
import codeemoji.core.CEMultiProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiTypeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.CESymbol.MANY;

public class SaysOneButContainsMany extends CEMultiProvider<NoSettings> {

    @Override
    public @Nullable String getPreviewText() {
        return """
                public class Customer {
                  private String[] name;
                }""";
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(@NotNull Editor editor) {
        List<InlayHintsCollector> collectors = new ArrayList<>();

        CEFieldCollector fieldCollector = new CEFieldCollector(editor, getKey().getId()) {
            @Override
            public void processInlay(@NotNull PsiField field, InlayHintsSink sink) {
                PsiTypeElement typeElement = field.getTypeElement();
                if (!CEUtil.isPluralForm(field.getName()) &&
                        !CEUtil.sameNameAsType(typeElement, field.getName()) &&
                        (CEUtil.isArrayType(typeElement) || CEUtil.isIterableType(typeElement))) {
                    addInlay(field.getNameIdentifier(), sink, MANY);
                }
            }
        };

        CELocalVariableCollector localVariableCollector = new CELocalVariableCollector(editor, getKey().getId()) {
            @Override
            public void processInlay(PsiElement element, InlayHintsSink sink) {

            }
        };

        collectors.add(fieldCollector);
        collectors.add(localVariableCollector);
        return collectors;
    }
}