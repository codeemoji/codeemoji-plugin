package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.ONE;

@SuppressWarnings("UnstableApiUsage")
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
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), ONE) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element){
                if ((element.getName().startsWith("get") || element.getName().startsWith("return"))
                        && CEUtils.isPluralForm(element.getName())) {
                    var typeElement = element.getReturnTypeElement();
                    return !CEUtils.isGenericType(element, typeElement) &&
                            (
                                    Objects.equals(element.getReturnType(), PsiTypes.voidType()) ||
                                            (!CEUtils.isArrayType(typeElement) &&
                                                    !CEUtils.isIterableType(typeElement) &&
                                                    !CEUtils.isMappableType(typeElement))
                            );
                }
                return false;
            }

        };
    }
}








