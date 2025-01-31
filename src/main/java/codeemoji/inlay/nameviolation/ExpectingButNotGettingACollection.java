package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.ONE;

public class ExpectingButNotGettingACollection extends CEProvider<ExpectingButNotGettingACollection.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "ExpectingButNotGettingACollectionSettings", storages = @Storage("codeemoji-expecting-but-not-getting-a-collection-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings>{
        public Settings(){
            super(ExpectingButNotGettingACollection.class, ONE);
        }
    }

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
        return new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
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








