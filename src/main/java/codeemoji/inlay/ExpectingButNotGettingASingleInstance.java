package codeemoji.inlay;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiTypes;

import java.util.Objects;

import static codeemoji.core.CESymbol.MANY;

public class ExpectingButNotGettingASingleInstance extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public Object[] getParameter() {
                        Object[] array = doSomething();
                        return array;
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean checkAddInlay(PsiMethod method) {
                if (method != null &&
                        (method.getName().startsWith("get") || method.getName().startsWith("return")) &&
                        !Objects.equals(method.getReturnType(), PsiTypes.voidType()) &&
                        !CEUtil.isPluralForm(method.getName())) {
                    PsiTypeElement typeElement = method.getReturnTypeElement();
                    return CEUtil.isArrayType(typeElement) || CEUtil.isIterableType(typeElement);
                }
                return false;
            }
        };
    }
}








