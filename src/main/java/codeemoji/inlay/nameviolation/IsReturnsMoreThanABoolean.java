package codeemoji.inlay.nameviolation;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.core.CEConstants.CONFUSED;

public class IsReturnsMoreThanABoolean extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
        return """
                public class Customer {
                    public String isHuman() {
                        return "Yes";
                    }
                }""";
    }

    @Override
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), CONFUSED) {
            @Override
            public boolean isHintable(@NotNull PsiMethod element) {
                return element.getName().startsWith("is") && !(Objects.equals(element.getReturnType(), PsiTypes.booleanType()) || Objects.equals(element.getReturnType(), PsiTypes.voidType()));
            }
        };

    }
}








