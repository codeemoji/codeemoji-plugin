package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.basic.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.ONE;

public class SaysManyButContainsOne extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                import java.util.*;
                        
                public class Customer {
                    private String names;
                      
                    public String getItem(String buffers, int devices) {
                        return doSomething(buffers, devices);
                    }
                     
                    public Object buildMyObject(int value) {
                        Object items = new MyObject();
                        items.calcData(names, value);
                        return item;
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), ONE) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element) {
                PsiTypeElement typeElement = element.getTypeElement();
                return typeElement != null &&
                        CEUtils.isPluralForm(element.getName()) &&
                        !typeElement.isInferredType() && //TODO: detect inferred type name
                        !CEUtils.isGenericType(element, typeElement) &&
                        !CEUtils.isNumericType(typeElement) &&
                        !CEUtils.isArrayType(typeElement) &&
                        !CEUtils.isIterableType(typeElement) &&
                        !CEUtils.sameNameAsType(typeElement, element.getName()) &&
                        !CEUtils.containsOnlySpecialCharacters(typeElement.getText());
            }
        };
    }
}