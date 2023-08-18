package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.basic.CEMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationConstants.MANY;

public class ExpectingButNotGettingASingleInstance extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public Object[] getParameter() {
                        doSomething();
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean checkHint(@NotNull PsiMethod element) {
                if ((element.getName().startsWith("get") || element.getName().startsWith("return")) && !Objects.equals(element.getReturnType(), PsiTypes.voidType()) && !CEUtils.isPluralForm(element.getName())) {
                    PsiTypeElement typeElement = element.getReturnTypeElement();
                    return CEUtils.isArrayType(typeElement) || CEUtils.isIterableType(typeElement);
                }
                return false;
            }
        };
    }
}








