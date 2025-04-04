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

import static codeemoji.inlay.nameviolation.NameViolationSymbols.MANY;

public class SaysOneButContainsMany extends CEProvider<SaysOneButContainsMany.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "SaysOneButContainsMany", storages = @Storage("codeemoji-says-one-but-contains-many-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {
        public Settings(){
            super(SaysOneButContainsMany.class, MANY);
        }
    }

    @Override
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleVariableCollector(editor, getKey(), mainSymbol()) {
            @Override
            public boolean needsInlay(@NotNull PsiVariable element){
                var typeElement = element.getTypeElement();
                return !CEUtils.isPluralForm(element.getName()) &&
                        !CEUtils.sameNameAsType(typeElement, element.getName()) &&
                        (CEUtils.isArrayType(typeElement) ||
                                CEUtils.isIterableType(typeElement) ||
                                (null != typeElement && typeElement.getType() instanceof PsiEllipsisType)
                        );
            }

        };
    }
}
