package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiEllipsisType;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.MANY;

@SuppressWarnings("UnstableApiUsage")
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
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEVariableCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean needsHint(@NotNull PsiVariable element) {
                var typeElement = element.getTypeElement();
                return !CEUtils.isPluralForm(element.getName()) &&
                        !CEUtils.sameNameAsType(typeElement, element.getName()) &&
                        (CEUtils.isArrayType(typeElement) ||
                                CEUtils.isIterableType(typeElement) ||
                                (null != typeElement && typeElement.getType() instanceof PsiEllipsisType)
                        );
            }

        };
    }
}
