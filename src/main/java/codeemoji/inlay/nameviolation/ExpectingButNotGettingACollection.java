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
    public @NotNull InlayHintsCollector buildCollector(@NotNull Editor editor) {
        return new CEMethodCollector(editor, getKey(), ONE) {
            @Override
            public boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
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








