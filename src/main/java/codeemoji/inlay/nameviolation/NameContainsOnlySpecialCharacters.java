package codeemoji.inlay.nameviolation;

import codeemoji.core.collector.base.simple.CESimpleVariableCollector;
import codeemoji.core.config.CEPSIType;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NameContainsOnlySpecialCharacters extends CEProvider<NameContainsOnlySpecialCharacters.Settings> {

    @EqualsAndHashCode(callSuper = true)
    @ToString
    @Data
    @State(name = "NameContainsOnlySpecialCharactersSettings", storages = @Storage("codeemoji-name-contains-only-special-characters-settings.xml"))
    public static class Settings extends CEBaseSettings<Settings> {
        public Settings() {
            super(CEPSIType.UNSPECIFIED, NameContainsOnlySpecialCharacters.class, NameViolationSymbols.CONFUSED);
        }
    }

    @Override
    protected void createCollectors(CEProvider<Settings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.add(new CESimpleVariableCollector(editor, this) {
            @Override
            public boolean needsInlay(@NotNull PsiVariable element) {
                return CEUtils.containsOnlySpecialCharacters(Objects.requireNonNull(element.getName()));
            }
        });
    }
}