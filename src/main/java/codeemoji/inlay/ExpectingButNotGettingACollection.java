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

import static codeemoji.core.CESymbol.ONE;

public class ExpectingButNotGettingACollection extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public byte getBytes(byte[] buffer) {
                        doSomething();
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKey().getId(), ONE) {
            @Override
            public boolean checkAddInlay(PsiMethod method) {
                if (method != null &&
                        (method.getName().startsWith("get") || method.getName().startsWith("return")) &&
                        CEUtil.isPluralForm(method.getName())) {
                    PsiTypeElement typeElement = method.getReturnTypeElement();
                    return CEUtil.isNotGenericType(typeElement) &&
                            (Objects.equals(method.getReturnType(), PsiTypes.voidType()) ||
                                    (!CEUtil.isArrayType(typeElement) && !CEUtil.isIterableType(typeElement)));
                }
                return false;
            }

        };
    }
}








