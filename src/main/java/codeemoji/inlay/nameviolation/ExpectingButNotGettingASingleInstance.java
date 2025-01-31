package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.MANY;

public class ExpectingButNotGettingASingleInstance extends CEProvider<ExpectingButNotGettingASingleInstance.Settings> {

    public static class Settings extends CEBaseSettings<Settings> {}

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
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), MANY) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element){
                if ((element.getName().startsWith("get") || element.getName().startsWith("return")) &&
                        !Objects.equals(element.getReturnType(), PsiTypes.voidType()) &&
                        !CEUtils.isPluralForm(element.getName())) {
                    var typeElement = element.getReturnTypeElement();
                    return !CEUtils.sameNameAsType(typeElement, element.getName()) &&
                            (CEUtils.isArrayType(typeElement) ||
                                    CEUtils.isIterableType(typeElement) ||
                                    CEUtils.isMappableType(typeElement));
                }
                return false;
            }
        };
    }
}








