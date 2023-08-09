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

import static codeemoji.core.CEConstants.ONE;

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
            public boolean isHintable(@NotNull PsiVariable element) {
                PsiTypeElement typeElement = element.getTypeElement();
                return typeElement != null &&
                        CEUtil.isPluralForm(element.getName()) &&
                        !typeElement.isInferredType() && //TODO: detect inferred type
                        !CEUtil.isGenericType(element, typeElement) &&
                        !CEUtil.isNumericType(typeElement) &&
                        !CEUtil.isArrayType(typeElement) &&
                        !CEUtil.isIterableType(typeElement) &&
                        !CEUtil.sameNameAsType(typeElement, element.getName()) &&
                        !CEUtil.containsOnlySpecialCharacters(typeElement.getText());
            }
        };
    }
}