package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.MANY;

@SuppressWarnings("UnstableApiUsage")
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
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), MANY) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element) {
                if ((element.getName().startsWith("get") || element.getName().startsWith("return")) && !Objects.equals(element.getReturnType(), PsiTypes.voidType()) && !CEUtils.isPluralForm(element.getName())) {
                    var typeElement = element.getReturnTypeElement();
                    return CEUtils.isArrayType(typeElement) || CEUtils.isIterableType(typeElement);
                }
                return false;
            }
        };
    }
}








