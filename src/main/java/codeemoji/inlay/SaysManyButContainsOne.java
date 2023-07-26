package codeemoji.inlay;

import codeemoji.core.CEFieldCollector;
import codeemoji.core.CEProvider;
import codeemoji.core.CEUtil;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiTypeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static codeemoji.core.CESymbol.ONE;

public class SaysManyButContainsOne extends CEProvider<NoSettings> {

    @Override
    public @Nullable String getPreviewText() {
        return """
                public class Customer {
                  private String names;
                }""";
    }

    @Override
    public InlayHintsCollector getCollector(@NotNull Editor editor, @NotNull String keyId) {
        return new CEFieldCollector(editor, keyId) {
            @Override
            public void processInlayHint(PsiField field, InlayHintsSink sink) {
                PsiTypeElement typeElement = field.getTypeElement();
                if (CEUtil.isPluralForm(field.getName()) && !CEUtil.isArrayType(typeElement) && !CEUtil.isIterableType(typeElement)) {
                    addInlayHint(field, sink, ONE);
                }
            }
        };
    }
}