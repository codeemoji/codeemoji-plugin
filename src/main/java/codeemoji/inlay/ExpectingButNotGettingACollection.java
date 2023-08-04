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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.core.CEConstants.ONE;

public class ExpectingButNotGettingACollection extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public byte getBytes() {
                        doSomething();
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), ONE) {
            @Override
            public boolean isHintable(@NotNull PsiMethod element) {
                if ((element.getName().startsWith("get") || element.getName().startsWith("return")) && CEUtil.isPluralForm(element.getName())) {
                    PsiTypeElement typeElement = element.getReturnTypeElement();
                    return !CEUtil.isGenericType(element, typeElement) &&
                            (Objects.equals(element.getReturnType(), PsiTypes.voidType()) ||
                                    (!CEUtil.isArrayType(typeElement) && !CEUtil.isIterableType(typeElement)));
                }
                return false;
            }

        };
    }
}








