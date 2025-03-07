package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
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

import static codeemoji.inlay.nameviolation.NameViolationSymbols.CONFUSED;

public class GetMoreThanAccessor extends CEProvider<GetMoreThanAccessor.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "GetMoreThanAccessorSettings", storages = @Storage("codeemoji-get-more-than-accessor-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {
        public Settings() {
            super(GetMoreThanAccessor.class, CONFUSED);
        }
    }

    @Override
    public @NotNull InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
            @Override
            public boolean needsInlay(@NotNull PsiMethod element){
                if (element.getName().startsWith("get") && !Objects.equals(element.getReturnType(), PsiTypes.voidType()) && null != element.getBody()) {
                    return 1 < element.getBody().getStatements().length && !"getInstance".equalsIgnoreCase(element.getName());
                }
                return false;
            }
        };
    }
}








