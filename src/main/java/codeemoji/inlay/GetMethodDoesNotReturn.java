package codeemoji.inlay;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;

import java.util.Objects;

import static codeemoji.core.CESymbol.CONFUSED;

public class GetMethodDoesNotReturn extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public void getName() {
                        doSomething();
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), CONFUSED) {
            @Override
            public boolean checkAddInlay(PsiMethod method) {
                return method != null &&
                        (method.getName().startsWith("get") || method.getName().startsWith("return")) &&
                        Objects.equals(method.getReturnType(), PsiTypes.voidType());
            }
        };
    }
}








