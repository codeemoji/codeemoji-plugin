package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEVariableCollector;
import codeemoji.core.provider.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.CONFUSED;

@SuppressWarnings("UnstableApiUsage")
public class NameSuggestsBooleanByTypeDoesNot extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                import java.util.*;
                        
                public class Customer {
                    private String isActive;
                     
                    public String getItem(int isItemEnabled) {
                        return doSomething(isItemEnabled);
                    }
                 
                    public int buildMyObject(Double value) {
                        Integer isActiveForField = parseName(isActive);
                        return isActiveForField.intValue();
                    }
                }""";
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull final Editor editor) {
        return new CEVariableCollector(editor, this.getKeyId(), CONFUSED) {
            @Override
            public boolean needsHint(@NotNull final PsiVariable element) {
                if (null != element.getName()) {
                    return 2 < element.getName().length() &&
                            element.getName().startsWith("is") && !element.getType().equals(PsiTypes.booleanType());
                }
                return false;
            }
        };
    }
}