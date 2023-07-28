package codeemoji.inlay;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static codeemoji.core.CESymbol.CONFUSED;

public class GetMoreThanAccessor extends CEProvider<NoSettings> {

    @Override
    public @Nullable String getPreviewText() {
        return """
                public class Customer {
                    public List<Item> getItems() {
                        if (items == null) {
                            List<Item> list = new ArrayList<>();
                            Sale sale = getSale();
                            for (Item i : sale.items()) {
                                if (i.confirmed()){
                                    list.add(i);
                                }
                            }
                            list = Collections.unmodifiableList(list);
                        }
                        return items;
                    }
                }""";
    }

    @Override
    public InlayHintsCollector getCollector(@NotNull Editor editor, @NotNull String keyId) {
        return new CEMethodCollector(editor, keyId) {
            @Override
            public void execute(@Nullable PsiMethod method, InlayHintsSink sink) {
                if (method != null && method.getName().startsWith("get") &&
                        !(Objects.equals(method.getReturnType(), PsiTypes.voidType())) &&
                        method.getBody() != null) {
                    if (method.getBody().getStatements().length > 1) {
                        addInlayHint(Objects.requireNonNull(method.getNameIdentifier()), sink, CONFUSED);
                    }
                }
            }
        };
    }
}








