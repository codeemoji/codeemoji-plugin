package codeemoji.inlay;

import codeemoji.core.CEMethodCollector;
import codeemoji.core.CEProvider;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codeemoji.core.CEConstants.CONFUSED;

public class GetMoreThanAccessor extends CEProvider<NoSettings> {

    @Override
    public String getPreviewText() {
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
    public InlayHintsCollector buildCollector(Editor editor) {
        return new CEMethodCollector(editor, getKeyId(), CONFUSED) {
            @Override
            public boolean isHintable(@NotNull PsiMethod element) {
                if (element.getName().startsWith("get") && !Objects.equals(element.getReturnType(), PsiTypes.voidType()) && element.getBody() != null) {
                    return element.getBody().getStatements().length > 1 && !element.getName().equalsIgnoreCase("getInstance");
                }
                return false;
            }
        };
    }
}








