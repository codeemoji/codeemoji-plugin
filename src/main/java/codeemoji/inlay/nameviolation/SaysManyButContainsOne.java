package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleVariableCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiEllipsisType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.ONE;

public class SaysManyButContainsOne extends CEProvider<SaysManyButContainsOne.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "SaysManyButContainsOne", storages = @Storage("codeemoji-says-many-but-contains-one-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {
        public Settings(){
            super(SaysManyButContainsOne.class, ONE);
        }
    }

    @Override
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleVariableCollector(editor, getKey(), mainSymbol()) {
            @Override
            public boolean needsInlay(@NotNull PsiVariable element){
                var typeElement = element.getTypeElement();
                return null != typeElement &&
                        CEUtils.isPluralForm(element.getName()) &&
                        !(typeElement.getType() instanceof PsiEllipsisType) &&
                        !typeElement.isInferredType() &&
                        !CEUtils.isGenericType(element, typeElement) &&
                        !CEUtils.isConstantName(element) &&
                        !CEUtils.isNumericType(typeElement) &&
                        !CEUtils.isArrayType(typeElement) &&
                        !CEUtils.isIterableType(typeElement) &&
                        !CEUtils.isMappableType(typeElement) &&
                        !CEUtils.sameNameAsType(typeElement, element.getName()) &&
                        !CEUtils.containsOnlySpecialCharacters(typeElement.getText());
            }
        };
    }
}