package codeemoji.inlay;

import codeemoji.core.*;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.core.CESymbol.MANY;

public class SaysOneButContainsMany extends CEMultiProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                import java.util.*;
                                
                public class Customer {
                  private String[] name;
                  
                  public String getItem(byte[] buffer, List value) {
                    return doSomething(buffer, value);
                  }
                  
                  public List<Object> transformValue(int value) {
                    List<Object> item = new ArrayList<>();
                    item.addAll(doSomething(value));
                    return item;
                  }
                }""";
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {
        List<InlayHintsCollector> collectors = new ArrayList<>();

        CEFieldCollector fieldCollector = new CEFieldCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean isHintable(@NotNull PsiField element) {
                return evaluate(element);
            }
        };

        CELocalVariableCollector localVariableCollector = new CELocalVariableCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean isHintable(@NotNull PsiLocalVariable element) {
                return evaluate(element);
            }
        };

        CEParameterCollector parameterCollector = new CEParameterCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean isHintable(@NotNull PsiParameter element) {
                return evaluate(element);
            }
        };

        collectors.add(fieldCollector);
        collectors.add(localVariableCollector);
        collectors.add(parameterCollector);
        return collectors;
    }

    private boolean evaluate(@NotNull PsiVariable element) {
        PsiTypeElement typeElement = element.getTypeElement();
        return !CEUtil.isPluralForm(element.getName()) &&
                !CEUtil.sameNameAsType(typeElement, element.getName()) &&
                (CEUtil.isArrayType(typeElement) || CEUtil.isIterableType(typeElement));
    }

}
