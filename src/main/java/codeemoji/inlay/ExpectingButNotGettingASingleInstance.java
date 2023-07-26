package codeemoji.inlay;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ExpectingButNotGettingASingleInstance extends CEProvider<NoSettings> {

    @Override
    public @Nullable String getPreviewText() {
        return """
                public class Customer {
                    public Object[] getParameter() {
                        Object[] array = doSomething();
                        return array;
                    }
                }""";
    }

    @Override
    public InlayHintsCollector getCollector(@NotNull Editor editor, @NotNull String keyId) {
        return new CEMethodCollector(editor, keyId) {
            @Override
            public void processInlayHint(@Nullable PsiMethod method, InlayHintsSink sink) {
                if (method != null &&
                        (method.getName().startsWith("get") || method.getName().startsWith("return")) &&
                        !Objects.equals(method.getReturnType(), PsiTypes.voidType()) &&
                        !(method.getName().endsWith("s"))) {
                    PsiTypeElement typeElement = method.getReturnTypeElement();
                    if (CEUtil.isArrayType(typeElement) || CEUtil.isIterableType(typeElement)) {
                        addInlayHint(method, sink, 0x0039);
                    }

                }
            }
        };
    }
}








